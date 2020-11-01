/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.incidents;

import ACCLiveTiming.monitor.client.SessionId;
import ACCLiveTiming.monitor.client.events.AfterPacketReceived;
import ACCLiveTiming.monitor.client.events.BroadcastingEventEvent;
import ACCLiveTiming.monitor.client.events.SessionChanged;
import ACCLiveTiming.monitor.client.events.SessionPhaseChanged;
import ACCLiveTiming.monitor.eventbus.Event;
import ACCLiveTiming.monitor.eventbus.EventBus;
import ACCLiveTiming.monitor.eventbus.EventListener;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.extensions.logging.LoggingExtension;
import ACCLiveTiming.monitor.networking.data.AccBroadcastingData;
import ACCLiveTiming.monitor.networking.data.BroadcastingEvent;
import ACCLiveTiming.monitor.networking.data.SessionInfo;
import ACCLiveTiming.monitor.networking.enums.BroadcastingEventType;
import ACCLiveTiming.monitor.networking.enums.SessionPhase;
import ACCLiveTiming.monitor.networking.enums.SessionType;
import ACCLiveTiming.monitor.utility.TimeUtils;
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
        extends AccClientExtension
        implements EventListener {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());

    /**
     * Incident counter for the different sessions.
     */
    private static Map<SessionId, Integer> incidentCounter = new HashMap<>();

    /**
     * Last accident that is waiting to be commited.
     */
    private Accident stagedAccident = null;
    /**
     * List of accidents that have happened.
     */
    private List<Accident> accidents = new LinkedList<>();
    /**
     * Timestamp for when the race session started.
     */
    private long raceStartTimestamp;
    /**
     * Table model for the incident panel table.
     */
    private IncidentTableModel model = new IncidentTableModel();

    public IncidentExtension() {
        this.panel = new IncidentPanel(this);
        EventBus.register(this);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }

    public IncidentTableModel getTableModel() {
        return model;
    }

    public List<Accident> getAccidents() {
        List<Accident> a = new LinkedList<>(accidents);
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
            if (now - stagedAccident.getTimestamp() > 1000) {
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
            stagedAccident = new Accident(sessionTime,
                    client.getModel().getCar(event.getCarId()),
                    sessionId);
        } else {
            float timeDif = stagedAccident.getLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new Accident(sessionTime,
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
        commitAccident(new Accident(client.getModel().getSessionInfo().getSessionTime(),
                client.getSessionId()));
    }

    private void commitAccident(Accident a) {
        List<Accident> newAccidents = new LinkedList<>();
        newAccidents.addAll(accidents);
        newAccidents.add(a.withIncidentNumber(getAndIncrementCounter(client.getSessionId())));
        accidents = newAccidents;
        model.setAccidents(accidents);
    }

    private int getAndIncrementCounter(SessionId sessionId) {
        int result = incidentCounter.getOrDefault(sessionId, 0);
        incidentCounter.put(sessionId, result + 1);
        return result;
    }

}
