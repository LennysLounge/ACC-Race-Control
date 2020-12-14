/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.client;

import ACCLiveTiming.monitor.client.events.AfterPacketReceived;
import ACCLiveTiming.monitor.client.events.BroadcastingEventEvent;
import ACCLiveTiming.monitor.client.events.CarConnect;
import ACCLiveTiming.monitor.client.events.CarDisconnect;
import ACCLiveTiming.monitor.client.events.EntryListCarUpdate;
import ACCLiveTiming.monitor.client.events.EntryListUpdate;
import ACCLiveTiming.monitor.client.events.RealtimeCarUpdate;
import ACCLiveTiming.monitor.client.events.RealtimeUpdate;
import ACCLiveTiming.monitor.client.events.RegistrationResult;
import ACCLiveTiming.monitor.client.events.SessionChanged;
import ACCLiveTiming.monitor.client.events.SessionPhaseChanged;
import ACCLiveTiming.monitor.client.events.TrackData;
import ACCLiveTiming.monitor.eventbus.EventBus;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.extensions.logging.LoggingExtension;
import ACCLiveTiming.monitor.networking.PrimitivAccBroadcastingClient;
import ACCLiveTiming.monitor.networking.data.BroadcastingEvent;
import ACCLiveTiming.monitor.networking.data.CarInfo;
import ACCLiveTiming.monitor.networking.data.RealtimeInfo;
import ACCLiveTiming.monitor.networking.data.SessionInfo;
import ACCLiveTiming.monitor.networking.data.TrackInfo;
import ACCLiveTiming.monitor.networking.enums.SessionPhase;
import ACCLiveTiming.monitor.networking.enums.SessionType;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class BasicAccBroadcastingClient extends PrimitivAccBroadcastingClient {

    private static final Logger LOG = Logger.getLogger(BasicAccBroadcastingClient.class.getName());

    /**
     * List of registered extensions.
     */
    private final List<AccClientExtension> extensions = new LinkedList<>();
    /**
     * Session ID for the current session.
     */
    private SessionId sessionId = new SessionId(SessionType.NONE, -1, 0);
    /**
     * Current Phase of the session.
     */
    private SessionPhase sessionPhase = SessionPhase.NONE;
    /**
     * Counter coutns how of a session has happened.
     */
    private final Map<SessionType, Integer> sessionCounter = new HashMap<>();
    /**
     * Counts how many packets have been received.
     */
    private static int packetCount = 0;
    /**
     * List of logging messages.
     */
    private final List<String> messages = new LinkedList<>();

    /**
     * Creates a new broadcasting client for ACC.
     */
    public BasicAccBroadcastingClient() {
    }

    public void registerExtension(AccClientExtension e) {
        e.setClient(this);
        extensions.add(e);
    }

    public void unregisterExtension(AccClientExtension e) {
        extensions.remove(e);
    }

    public List<LPContainer> getPanels() {
        return extensions.stream()
                .filter(extension -> extension.hasPanel())
                .map(extension -> extension.getPanel())
                .collect(Collectors.toList());
    }

    public int getPacketCount() {
        return packetCount;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public void log(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    
    /*
    @Override
    protected void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message) {
        super.onRegistrationResult(connectionID, success, readOnly, message);
        EventBus.publish(new RegistrationResult(connectionID, success, readOnly, message));
    }
    @Override
    protected void onRealtimeUpdate(SessionInfo sessionInfo) {
        super.onRealtimeUpdate(sessionInfo);

        if (sessionId.getIndex() != sessionInfo.getSessionIndex()) {
            //fast forward currnet session to result UI
            while (sessionPhase != SessionPhase.RESULTUI) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChaged(sessionPhase, sessionInfo);
            }
            //Move to next sessionId;
            SessionType type = sessionInfo.getSessionType();
            int sessionIndex = sessionInfo.getSessionIndex();
            int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
            sessionCounter.put(type, sessionNumber);

            SessionId newSessionId = new SessionId(type, sessionIndex, sessionNumber);
            onSessionChanged(newSessionId, sessionInfo);
            sessionId = newSessionId;

            sessionPhase = SessionPhase.NONE;
        }
        //Fast forward to current phase
        while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
            sessionPhase = SessionPhase.getNext(sessionPhase);
            onSessionPhaseChaged(sessionPhase, sessionInfo);
        }
        EventBus.publish(new RealtimeUpdate(sessionInfo));
    }

    @Override
    protected void onRealtimeCarUpdate(RealtimeInfo info) {
        super.onRealtimeCarUpdate(info);
    }

    @Override
    protected void onEntryListUpdate(List<Integer> carIds) {
        //Find cars that are active but not in this update and fire 
        //the disconnect event for them.
        getModel().getCarsInfo().values().stream()
                .filter(carInfo -> carInfo.isConnected())
                .filter(carInfo -> !carIds.contains(carInfo.getCarId()))
                .forEach(carInfo -> onCarDisconnect(carInfo.withConnected(false)));

        super.onEntryListUpdate(carIds);
        EventBus.publish(new EntryListUpdate(carIds));
    }

    @Override
    protected void onTrackData(TrackInfo info) {
        super.onTrackData(info);
    }

    @Override
    protected void onEntryListCarUpdate(CarInfo carInfo) {
        //if there is an update for a car that is disconnected then
        //we fire the connected event for that car
        if (getModel().getCarsInfo().containsKey(carInfo.getCarId())) {
            if (!getModel().getCar(carInfo.getCarId()).isConnected()) {
                onCarConnect(carInfo);
            }
        }
        super.onEntryListCarUpdate(carInfo);
        EventBus.publish(new EntryListCarUpdate(carInfo));
    }

    @Override
    protected void onBroadcastingEvent(BroadcastingEvent event) {
        super.onBroadcastingEvent(event);
        EventBus.publish(new BroadcastingEventEvent(event));
    }

    @Override
    protected void afterPacketReceived(byte type) {
        super.afterPacketReceived(type);
        packetCount++;
        EventBus.publish(new AfterPacketReceived(type, packetCount));
    }
    */

    private void onSessionChanged(SessionId newId, SessionInfo info) {
        LOG.info("session changed to " + newId.getType().name() + " Index:" + newId.getIndex() + " sessionCount:" + newId.getNumber());
        EventBus.publish(new SessionChanged(newId, info));
    }

    private void onSessionPhaseChaged(SessionPhase phase, SessionInfo info) {
        LOG.info("session phase changed to " + phase.name());
        //Create sessionInfo object with the correct sessionPhase
        SessionInfo correctedSessionInfo = new SessionInfo(info.getEventIndex(),
                info.getSessionIndex(), info.getSessionType(), phase,
                info.getSessionTime(), info.getSessionEndTime(), info.getFocusedCarIndex(),
                info.getActiveCameraSet(), info.getActiveCamera(), info.getCurrentHudPage(),
                info.getIsReplayPlaying(), info.getReplaySessionTime(), info.getReplayRemainingTime(),
                info.getTimeOfDay(), info.getAmbientTemp(), info.getTrackTemp(),
                info.getCloudLevel(), info.getRainLevel(), info.getWetness(),
                info.getBestSessionLap());
        EventBus.publish(new SessionPhaseChanged(correctedSessionInfo));
    }

    private void onCarDisconnect(CarInfo car) {
        String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
        LoggingExtension.log("Car disconnected: #" + car.getCarNumber() + "\t" + name);
        LOG.info("Car disconnected: #" + car.getCarNumber() + "\t" + name);
        EventBus.publish(new CarDisconnect(car));
    }

    private void onCarConnect(CarInfo car) {
        String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
        LoggingExtension.log("Car connected: #" + car.getCarNumber() + "\t" + name);
        LOG.info("Car connected: #" + car.getCarNumber() + "\t" + name);
        EventBus.publish(new CarConnect(car));
    }
}
