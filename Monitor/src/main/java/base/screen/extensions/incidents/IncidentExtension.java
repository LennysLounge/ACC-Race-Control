/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents;

import base.screen.Main;
import base.screen.networking.SessionId;
import base.screen.networking.events.AfterPacketReceived;
import base.screen.networking.BroadcastingEventEvent;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.GeneralExtentionConfigPanel;
import base.screen.extensions.logging.LoggingExtension;
import base.screen.networking.data.AccBroadcastingData;
import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.enums.BroadcastingEventType;
import base.screen.utility.TimeUtils;
import base.screen.extensions.incidents.events.Accident;
import base.screen.networking.AccBroadcastingClient;
import base.screen.visualisation.gui.LPContainer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class IncidentExtension
        implements EventListener, AccClientExtension {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());
    /**
     * Reference to the client.
     */
    private final AccBroadcastingClient client;
    /**
     * The visualisation for this extension.
     */
    private final IncidentPanel panel;
    /**
     * Incident counter for the different sessions.
     */
    private static Map<SessionId, Integer> incidentCounter = new HashMap<>();

    /**
     * Last accident that is waiting to be commited.
     */
    private IncidentInfo stagedAccident = null;
    /**
     * List of accidents that have happened.
     */
    private List<IncidentInfo> accidents = new LinkedList<>();
    /**
     * Timestamp for when the race session started.
     */
    private long raceStartTimestamp;
    /**
     * Table model for the incident panel table.
     */
    private IncidentTableModel model = new IncidentTableModel();

    public IncidentExtension() {
        this.client = Main.getClient();
        this.panel = new IncidentPanel(this);
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        if (GeneralExtentionConfigPanel.getInstance().isIncidentLogEnabled()) {
            return panel;
        }
        return null;
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
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
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            if (event.getType() == BroadcastingEventType.ACCIDENT) {
                onAccident(event);
            }
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
        String logMessage = "Accident: #" + client.getModel().getCar(event.getCarId()).getCarNumber()
                + "\t" + TimeUtils.asDuration(client.getModel().getSessionInfo().getSessionTime());
        LoggingExtension.log(logMessage);
        LOG.info(logMessage);

        float sessionTime = client.getModel().getSessionInfo().getSessionTime();
        SessionId sessionId = client.getSessionId();
        if (stagedAccident == null) {
            stagedAccident = new IncidentInfo(sessionTime,
                    client.getModel().getCar(event.getCarId()),
                    sessionId);
        } else {
            float timeDif = stagedAccident.getSessionLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new IncidentInfo(sessionTime,
                        client.getModel().getCar(event.getCarId()),
                        sessionId);
            } else {
                stagedAccident = stagedAccident.addCar(sessionTime,
                        client.getModel().getCar(event.getCarId()),
                        System.currentTimeMillis());
            }
        }
    }

    public void addEmptyAccident() {
        commitAccident(new IncidentInfo(client.getModel().getSessionInfo().getSessionTime(),
                client.getSessionId()));
    }

    private void commitAccident(IncidentInfo a) {
        List<IncidentInfo> newAccidents = new LinkedList<>();
        newAccidents.addAll(accidents);
        newAccidents.add(a);
        //newAccidents.add(a.withIncidentNumber(getAndIncrementCounter(client.getSessionId())));
        accidents = newAccidents;
        model.setAccidents(accidents);

        EventBus.publish(new Accident(a));
        panel.invalidate();
    }

    private int getAndIncrementCounter(SessionId sessionId) {
        int result = incidentCounter.getOrDefault(sessionId, 0);
        incidentCounter.put(sessionId, result + 1);
        return result;
    }

    @Override
    public void removeExtension() {
        EventBus.unregister(this);
    }

}
