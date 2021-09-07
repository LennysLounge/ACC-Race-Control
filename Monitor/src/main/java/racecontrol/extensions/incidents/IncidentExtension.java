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
import racecontrol.client.extension.AccClientExtension;
import racecontrol.app.components.GeneralExtentionConfigPanel;
import racecontrol.client.data.AccBroadcastingData;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.enums.BroadcastingEventType;
import racecontrol.utility.TimeUtils;
import racecontrol.extensions.incidents.events.Accident;
import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.extensions.replayoffset.ReplayStart;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.lpgui.gui.LPContainer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.client.data.CarInfo;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.client.events.SessionChanged;
import racecontrol.logging.UILogger;

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
    private List<IncidentEntry> accidents = new LinkedList<>();
    /**
     * Table model for the incident panel table.
     */
    private final IncidentTableModel model;
    /**
     * Flag indicates that the replay offset is known.
     */
    private boolean replayTimeKnown = false;
    /**
     * Reference to the replay offset extension
     */
    private final ReplayOffsetExtension replayOffsetExtension;

    public IncidentExtension(AccBroadcastingClient client) {
        super(client);
        this.model = new IncidentTableModel(this);
        this.panel = new IncidentPanel(this);
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
        } else if (e instanceof SessionChanged) {
            List<IncidentEntry> newAccidents = new LinkedList<>();
            newAccidents.addAll(accidents);
            switch (((SessionChanged) e).getSessionInfo().getSessionType()) {
                case PRACTICE:
                    newAccidents.add(new IncidentEntry(IncidentEntry.Divider.PRACTICE));
                    break;
                case QUALIFYING:
                    newAccidents.add(new IncidentEntry(IncidentEntry.Divider.QUALIFYING));
                    break;
                case RACE:
                    newAccidents.add(new IncidentEntry(IncidentEntry.Divider.RACE));
                    break;
            }
            accidents = newAccidents;
            model.setAccidents(accidents);
            panel.invalidate();
        } else if (e instanceof RealtimeUpdate) {
            //create a list of all currently connected cars at tell the model about it.
            model.setConnectedCars(new LinkedList<>(getClient().getModel().getCarsInfo().keySet()));
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
                + "\t\t" + TimeUtils.asDuration(sessionTime)
                + "\t" + TimeUtils.asDuration(replayOffsetExtension.getReplayTimeFromConnectionTime(event.getTimeMs()));
        UILogger.log(logMessage);
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
        List<IncidentEntry> newAccidents = new LinkedList<>();
        newAccidents.addAll(accidents);
        newAccidents.add(new IncidentEntry(a));
        accidents = newAccidents;
        model.setAccidents(accidents);

        EventBus.publish(new Accident(a));
        panel.invalidate();
    }

    private void updateAccidentsWithReplayTime() {
        SessionId currentSessionId = getClient().getSessionId();
        List<IncidentEntry> newAccidents = new LinkedList<>();
        for (IncidentEntry incidentEntry : accidents) {
            if (incidentEntry.getDividerType() != IncidentEntry.Divider.NONE) {
                newAccidents.add(incidentEntry);
            } else {
                IncidentInfo incident = incidentEntry.getIncident();
                if (incident.getSessionID().equals(currentSessionId)) {
                    newAccidents.add(
                            new IncidentEntry(
                                    incident.withReplayTime(
                                            replayOffsetExtension.getReplayTimeFromSessionTime((int) incident.getSessionEarliestTime())
                                    )
                            )
                    );
                }
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

    public void createDummyIncident() {
        if (getClient().getModel().getCarsInfo().isEmpty()) {
            return;
        }
        LOG.info("Create dummy accident.");
        int nCars = (int) Math.floor(Math.random() * Math.min(6, getClient().getModel().getCarsInfo().size()) + 1);
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        IncidentInfo incident = new IncidentInfo(
                sessionTime,
                replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime),
                getClient().getSessionId());
        for (int i = 0; i < nCars; i++) {
            incident = incident.addCar(
                    sessionTime,
                    getRandomCar(),
                    0);
        }
        commitAccident(incident);
    }

    private CarInfo getRandomCar() {
        int r = (int) Math.floor(Math.random() * getClient().getModel().getCarsInfo().size());
        int i = 0;
        for (CarInfo car : getClient().getModel().getCarsInfo().values()) {
            if (i++ == r) {
                return car;
            }
        }
        return null;
    }

    public void focusOnCar(int carId) {
        LOG.info("Setting focus on car " + carId);
        getClient().sendChangeFocusRequest(carId);
    }

    public void startAccidentReplay(IncidentInfo incident, int seconds) {
        LOG.info("Starting instant replay for incident for " + seconds);
        getClient().sendInstantReplayRequestWithCamera(
                incident.getSessionEarliestTime() - seconds * 1000f - 5000,
                seconds + 5,
                getClient().getModel().getSessionInfo().getFocusedCarIndex(),
                getClient().getModel().getSessionInfo().getActiveCameraSet(),
                getClient().getModel().getSessionInfo().getActiveCamera()
        );
    }

}
