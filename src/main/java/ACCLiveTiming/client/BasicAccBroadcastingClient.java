/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.client;

import ACCLiveTiming.networking.PrimitivAccBroadcastingClient;
import ACCLiveTiming.networking.data.BroadcastingEvent;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.data.SessionInfo;
import ACCLiveTiming.networking.data.TrackInfo;
import ACCLiveTiming.networking.enums.SessionPhase;
import ACCLiveTiming.networking.enums.SessionType;
import java.net.SocketException;
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
     * A map of the session events. Maps a session Type and a session phase to a
     * method.
     */
    private final Map<SessionType, Map<SessionPhase, Runnable>> sessionEvents = new HashMap<>();
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

    public BasicAccBroadcastingClient() throws SocketException {
        super();
        //Init session events.
        Map<SessionPhase, Runnable> practiceEvents = new HashMap<>();
        practiceEvents.put(SessionPhase.STARTING, this::onPracticeStart);
        practiceEvents.put(SessionPhase.POSTSESSION, this::onPracticeEnd);

        Map<SessionPhase, Runnable> qualiEvents = new HashMap<>();
        qualiEvents.put(SessionPhase.STARTING, this::onQualifyingStart);
        qualiEvents.put(SessionPhase.POSTSESSION, this::onQualifyingEnd);

        Map<SessionPhase, Runnable> raceEvents = new HashMap<>();
        raceEvents.put(SessionPhase.STARTING, this::onRaceStart);
        raceEvents.put(SessionPhase.POSTSESSION, this::onRaceEnd);

        sessionEvents.put(SessionType.PRACTICE, practiceEvents);
        sessionEvents.put(SessionType.QUALIFYING, qualiEvents);
        sessionEvents.put(SessionType.RACE, raceEvents);
    }

    public void registerExtension(AccClientExtension e) {
        e.setClient(this);
        extensions.add(e);
    }

    public void unregisterExtension(AccClientExtension e) {
        extensions.remove(e);
    }

    public List<ExtensionPanel> getPanels() {
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

    @Override
    protected void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message) {
        super.onRegistrationResult(connectionID, success, readOnly, message);
        extensions.forEach(extension -> extension.onRegistrationResult(connectionID, success, readOnly, message));
    }

    @Override
    protected void onRealtimeUpdate(SessionInfo sessionInfo) {
        super.onRealtimeUpdate(sessionInfo);

        if (sessionId.getIndex() != sessionInfo.getSessionIndex()) {
            LOG.info("session Index mismatch");
            //fast forward currnet session to result UI
            while (sessionPhase != SessionPhase.RESULTUI) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                runSessionEvent(sessionId.getType(), sessionPhase);
            }
            //Move to next sessionId;
            SessionType type = sessionInfo.getSessionType();
            int sessionIndex = sessionInfo.getSessionIndex();
            int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
            sessionCounter.put(type, sessionNumber);
            
            SessionId newSessionId = new SessionId(type, sessionIndex, sessionNumber);
            onSessionChanged(sessionId, newSessionId);
            sessionId = newSessionId;
            
            sessionPhase = SessionPhase.NONE;
            LOG.info("session + " + type.name() + " started. Session nr:" + sessionNumber + " sessionIndex:" + sessionIndex);
        }
        //Fast forward to current phase
        while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
            sessionPhase = SessionPhase.getNext(sessionPhase);
            runSessionEvent(sessionId.getType(), sessionPhase);
        }

        extensions.forEach(extension -> extension.onRealtimeUpdate(sessionInfo));
    }

    @Override
    protected void onRealtimeCarUpdate(RealtimeInfo info) {
        super.onRealtimeCarUpdate(info);
        extensions.forEach(extension -> extension.onRealtimeCarUpdate(info));
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
        extensions.forEach(extension -> extension.onEntryListUpdate(carIds));
    }

    @Override
    protected void onTrackData(TrackInfo info) {
        super.onTrackData(info);
        extensions.forEach(extension -> extension.onTrackData(info));
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
        extensions.forEach(extension -> extension.onEntryListCarUpdate(carInfo));
    }

    @Override
    protected void onBroadcastingEvent(BroadcastingEvent event) {
        super.onBroadcastingEvent(event);
        extensions.forEach(extension -> extension.onBroadcastingEvent(event));
        switch (event.getType()) {
            case ACCIDENT:
                onAccident(event);
                break;
            case LAPCOMPLETED:
                onLapComplete(event);
                break;
            case BESTPERSONALLAP:
                onBestPersonalLap(event);
                break;
            case BESTSESSIONLAP:
                onBestSessionLap(event);
                break;
        }
    }

    @Override
    protected void afterPacketReceived(byte type) {
        super.afterPacketReceived(type);
        packetCount++;
        extensions.forEach(extension -> extension.afterPacketReceived(type));
    }

    private void runSessionEvent(SessionType type, SessionPhase phase) {
        Map<SessionPhase, Runnable> events = sessionEvents.get(type);
        if (events != null) {
            Runnable event = events.get(phase);
            if (event != null) {
                event.run();
                return;
            }
        }
        LOG.info("no method found for type:" + type.name() + ", phase:" + phase.name());
    }

    private void onSessionChanged(SessionId oldId, SessionId newId) {
        extensions.forEach(extension -> extension.onSessionChanged(oldId, newId));
    }

    private void onAccident(BroadcastingEvent event) {
        extensions.forEach(extension -> extension.onAccident(event));
    }

    private void onLapComplete(BroadcastingEvent event) {
        extensions.forEach(extension -> extension.onLapComplete(event));
    }

    private void onBestSessionLap(BroadcastingEvent event) {
        extensions.forEach(extension -> extension.onBestSessionLap(event));
    }

    private void onBestPersonalLap(BroadcastingEvent event) {
        extensions.forEach(extension -> extension.onBestPersonalLap(event));
    }

    private void onPracticeStart() {
        log("Practice starting");
        LOG.info("Practice starting");
        extensions.forEach(extension -> extension.onPracticeStart());
    }

    private void onPracticeEnd() {
        log("Practice post-session");
        LOG.info("Practice post-session");
        extensions.forEach(extension -> extension.onPracticeEnd());
    }

    private void onQualifyingStart() {
        log("Qualifying starting");
        LOG.info("Qualfifying starting");
        extensions.forEach(extension -> extension.onQualifyingStart());
    }

    private void onQualifyingEnd() {
        log("Qualifying post-session");
        LOG.info("Qualfifying post-session");
        extensions.forEach(extension -> extension.onQualifyingEnd());
    }

    private void onRaceStart() {
        log("Race starting");
        LOG.info("Race starting");
        extensions.forEach(extension -> extension.onRaceStart());
    }

    private void onRaceEnd() {
        log("Race post-session");
        LOG.info("Race post-session");
        extensions.forEach(extension -> extension.onRaceEnd());
    }

    private void onCarDisconnect(CarInfo car) {
        String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
        log("Car disconnected: #" + car.getCarNumber() + "\t" + name);
        LOG.info("Car disconnected: #" + car.getCarNumber() + "\t" + name);
        extensions.forEach(extension -> extension.onCarDisconnect(car));
    }

    private void onCarConnect(CarInfo car) {
        String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
        log("Car connected: #" + car.getCarNumber() + "\t" + name);
        LOG.info("Car connected: #" + car.getCarNumber() + "\t" + name);
        extensions.forEach(extension -> extension.onCarConnect(car));
    }
}
