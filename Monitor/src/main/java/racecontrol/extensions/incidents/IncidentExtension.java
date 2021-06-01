/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

import racecontrol.client.data.SessionId;
import racecontrol.client.events.AfterPacketReceived;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.extensions.GeneralExtentionConfigPanel;
import racecontrol.extensions.logging.LoggingExtension;
import racecontrol.client.data.AccBroadcastingData;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.enums.BroadcastingEventType;
import racecontrol.utility.TimeUtils;
import racecontrol.extensions.incidents.events.Accident;
import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.extensions.replayoffset.ReplayStart;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.visualisation.gui.LPContainer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class IncidentExtension
        extends AccClientExtension {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(IncidentExtension.class.getName());
    /**
     * The visualisation for this extension.
     */
    private final IncidentPanel panel;
    /**
     * Last accident that is waiting to be commited.
     */
    private IncidentInfo stagedAccident = null;
    /**
     * List of accidents that have happened.
     */
    private List<IncidentInfo> accidents = new LinkedList<>();
    /**
     * Table model for the incident panel table.
     */
    private final IncidentTableModel model = new IncidentTableModel();
    /**
     * Flag indicates that the replay offset is known.
     */
    private boolean replayTimeKnown = false;
    /**
     * Reference to the logging extension.
     */
    private final LoggingExtension loggingExtension;
    /**
     * Reference to the replay offset extension
     */
    private final ReplayOffsetExtension replayOffsetExtension;

    public IncidentExtension(AccBroadcastingClient client) {
        super(client);
        this.panel = new IncidentPanel(this);
        loggingExtension = client.getOrCreateExtension(LoggingExtension.class);
        replayOffsetExtension = client.getOrCreateExtension(ReplayOffsetExtension.class);
    }

    @Override
    public LPContainer getPanel() {
        if (GeneralExtentionConfigPanel.getInstance().isIncidentLogEnabled()) {
            return panel;
        }
        return null;
    }

    public AccBroadcastingData getModel() {
        return getClient().getModel();
    }

    public IncidentTableModel getTableModel() {
        return model;
    }

    public List<IncidentInfo> getAccidents() {
        List<IncidentInfo> a = new LinkedList<>(accidents);
        Collections.reverse(a);
        return Collections.unmodifiableList(a);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof AfterPacketReceived) {
            afterPacketReceived(((AfterPacketReceived) e).getType());
            if (!replayTimeKnown && replayOffsetExtension.requireSearch()) {
                panel.enableSearchButton();
            }
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            if (event.getType() == BroadcastingEventType.ACCIDENT) {
                onAccident(event);
            }
        } else if (e instanceof ReplayStart) {
            replayTimeKnown = true;
            panel.setReplayOffsetKnown();
            updateAccidentsWithReplayTime();
        }
    }

    public void afterPacketReceived(byte type) {
        if (stagedAccident != null) {
            long now = System.currentTimeMillis();
            if (now - stagedAccident.getSystemTimestamp() > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = null;
            }
        }
    }

    public void onAccident(BroadcastingEvent event) {
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        String logMessage = "Accident: #" + getClient().getModel().getCar(event.getCarId()).getCarNumber()
                + "\t" + TimeUtils.asDuration(sessionTime)
                + "\t" + TimeUtils.asDuration(replayOffsetExtension.getReplayTimeFromConnectionTime(event.getTimeMs()));
        loggingExtension.log(logMessage);
        LOG.info(logMessage);

        SessionId sessionId = getClient().getSessionId();
        if (stagedAccident == null) {
            stagedAccident = new IncidentInfo(sessionTime,
                    replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                    getClient().getModel().getCar(event.getCarId()),
                    sessionId);
        } else {
            float timeDif = stagedAccident.getSessionLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new IncidentInfo(sessionTime,
                        replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                        getClient().getModel().getCar(event.getCarId()),
                        sessionId);
            } else {
                stagedAccident = stagedAccident.addCar(sessionTime,
                        getClient().getModel().getCar(event.getCarId()),
                        System.currentTimeMillis());
            }
        }
    }

    public void addEmptyAccident() {
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        commitAccident(new IncidentInfo(sessionTime,
                replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                getClient().getSessionId()));
    }

    private void commitAccident(IncidentInfo a) {
        List<IncidentInfo> newAccidents = new LinkedList<>();
        newAccidents.addAll(accidents);
        newAccidents.add(a);
        accidents = newAccidents;
        model.setAccidents(accidents);

        EventBus.publish(new Accident(a));
        panel.invalidate();
    }

    private void updateAccidentsWithReplayTime() {
        SessionId currentSessionId = getClient().getSessionId();
        List<IncidentInfo> newAccidents = new LinkedList<>();
        for (IncidentInfo incident : accidents) {
            if (incident.getSessionID().equals(currentSessionId)) {
                newAccidents.add(incident.withReplayTime(
                        replayOffsetExtension.getReplayTimeFromSessionTime((int) incident.getSessionEarliestTime())
                ));
            }
        }
        accidents = newAccidents;
        model.setAccidents(accidents);
        panel.invalidate();

        if (stagedAccident != null) {
            stagedAccident = stagedAccident.withReplayTime(
                    replayOffsetExtension.getReplayTimeFromSessionTime((int) stagedAccident.getSessionEarliestTime())
            );
        }
    }

    public void findReplayOffset() {
        replayOffsetExtension.findSessionChange();
    }

}
