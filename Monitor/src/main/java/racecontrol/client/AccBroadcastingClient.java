/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client;

import racecontrol.client.data.SessionId;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.CarDisconnectedEvent;
import racecontrol.client.events.EntryListCarUpdateEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RegistrationResultEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.events.TrackDataEvent;
import racecontrol.eventbus.EventBus;
import racecontrol.client.data.AccBroadcastingData;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.data.enums.SessionPhase;
import racecontrol.client.data.enums.SessionType;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.events.EntryListUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.ReplayEndedEvent;
import racecontrol.client.events.ReplayStartedEvent;
import racecontrol.client.extension.contact.ContactExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIController;
import racecontrol.client.extension.laptimes.LapTimeExtension;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.logging.UILogger;
import racecontrol.client.extension.racereport.RaceReportController;
import racecontrol.client.extension.results.ResultsExtension;
import racecontrol.client.extension.statistics.StatisticsExtension;

/**
 * A basic connection to the broadcasting interface from Assetto Corsa
 * Competizione.
 *
 * @author Leonard
 */
public class AccBroadcastingClient {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(AccBroadcastingClient.class.getName());
    /**
     * Singelton instance.
     */
    private static AccBroadcastingClient instance;
    /**
     * Address where the ACC broadcast server is running.
     */
    private InetAddress hostAddress = null;
    /**
     * Port where the ACC broadcast server is running.
     */
    private int hostPort;
    /**
     * Display name of this connection.
     */
    private String displayName = "";
    /**
     * Connection password.
     */
    private String connectionPassword = "";
    /**
     * Command password.
     */
    private String commandPassword = "";
    /**
     * Interval in which to receive updated in ms.
     */
    private int updateInterval;
    /**
     * Socket used for the connection.
     */
    private DatagramSocket socket;
    /**
     * Thread where the connection loop is running.
     */
    private UdpListener accListenerThread;
    /**
     * Model that holds the data.
     */
    private AccBroadcastingData model = new AccBroadcastingData();
    /**
     * Time when the entry list was requested.
     */
    private long lastTimeEntryListRequest = 0;
    /**
     * Session ID for the current session.
     */
    private SessionId sessionId = new SessionId(SessionType.NONE, -1, 0);
    /**
     * Current Phase of the session.
     */
    private SessionPhase sessionPhase = SessionPhase.NONE;
    /**
     * Counter coutns how many of a session have happened.
     */
    private final Map<SessionType, Integer> sessionCounter = new HashMap<>();
    /**
     * Counts how many packets have been received.
     */
    private static int packetCount = 0;
    /**
     * List of broadcast extensions.
     */
    private final List<AccBroadcastingExtension> extensions = new ArrayList<>();

    private AccBroadcastingClient() {
    }

    public void initialise() {
        //instanciate extensions
        extensions.add(ContactExtension.getInstance());
        extensions.add(GoogleSheetsAPIController.getInstance());
        extensions.add(new LapTimeExtension());
        extensions.add(RaceReportController.getInstance());
        extensions.add(ReplayOffsetExtension.getInstance());
        extensions.add(new ResultsExtension());
        extensions.add(StatisticsExtension.getInstance());
    }

    /**
     * Creates and gets a singleton instance of this class.
     *
     * @return the singelton instance of this class.
     */
    public static AccBroadcastingClient getClient() {
        if (instance == null) {
            instance = new AccBroadcastingClient();
        }
        return instance;
    }

    /**
     * Connects to the game client.
     *
     * @param displayName The display name of this connection.
     * @param connectionPassword The password for this connection.
     * @param commandPassword The command password.
     * @param updateInterval The interval in which to receive updates.
     * @param hostAddress Host address of the server.
     * @param hostPort Host port of the server.
     * @throws java.net.SocketException
     */
    public void connect(String displayName,
            String connectionPassword,
            String commandPassword,
            int updateInterval,
            InetAddress hostAddress,
            int hostPort) throws SocketException {
        this.displayName = requireNonNull(displayName, "displayName");
        this.connectionPassword = requireNonNull(connectionPassword, "connectionPassword");
        this.commandPassword = requireNonNull(commandPassword, "commandPassword");
        if (updateInterval < 0) {
            throw new IllegalArgumentException("Update interval cannot be less than 0");
        }
        this.updateInterval = updateInterval;
        this.hostAddress = requireNonNull(hostAddress, "hostAddress");
        this.hostPort = requireNonNull(hostPort, "hostPort");

        //create socket
        socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        socket.connect(this.hostAddress, this.hostPort);

        //create new data model and sessionId
        model = new AccBroadcastingData();
        sessionId = new SessionId(SessionType.NONE, -1, 0);

        startListernerThread();
    }

    private void startListernerThread() {
        accListenerThread = new UdpListener("ACC listener thread");
        accListenerThread.start();
    }

    /**
     * Blocks until the connection to the game is closed.
     *
     * @return 0 for normal exit, 1 for abnormal exit.
     */
    public ExitState waitForFinish() {
        try {
            accListenerThread.join();
        } catch (InterruptedException ex) {
            LOG.info("exceptioN!!!!");
        }
        return accListenerThread.getExitState();
    }

    public void stopAndKill() {
        LOG.info("interupting listener");
        accListenerThread.interrupt();
    }

    /**
     * Gives the update interval for the connection.
     *
     * @return The update interval.
     */
    public int getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Returns the current model.
     *
     * @return The current data model.
     */
    public AccBroadcastingData getModel() {
        return model;
    }

    /**
     * Returns true if the socket is connected and currently listening for a
     * packet.
     *
     * @return True when connected.
     */
    public boolean isConnected() {
        if (socket != null
                && socket.isConnected()
                && accListenerThread != null
                && accListenerThread.isAlive()
                && accListenerThread.getExitState() == ExitState.NONE) {
            return true;
        }
        return false;
    }

    /**
     * Returns the ammount of received packets.
     *
     * @return the current packet count.
     */
    public int getPacketCount() {
        return packetCount;
    }

    /**
     * Returns the current SessionId object.
     *
     * @return the current SessionId.
     */
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * Send a register command.
     *
     */
    public void sendRegisterRequest() {
        sendRequest(AccBroadcastingProtocol.buildRegisterRequest(displayName,
                connectionPassword,
                updateInterval,
                commandPassword
        ));
    }

    /**
     * Send unregister command.
     *
     */
    public void sendUnregisterRequest() {
        sendRequest(AccBroadcastingProtocol.buildUnregisterRequest(
                model.getConnectionID()
        ));
    }

    /**
     * Send a request for the current entry list.
     *
     */
    public void sendEntryListRequest() {
        lastTimeEntryListRequest = System.currentTimeMillis();
        sendRequest(AccBroadcastingProtocol.buildEntryListRequest(
                model.getConnectionID()
        ));
    }

    /**
     * Send a request for the current track data.
     *
     */
    public void sendTrackDataRequest() {
        sendRequest(AccBroadcastingProtocol.buildTrackDataRequest(
                model.getConnectionID()
        ));
    }

    /**
     * Send a request to change the currently focused car.
     *
     * @param carIndex the car index of the car to focus on.
     */
    public void sendChangeFocusRequest(int carIndex) {
        if (!model.getCarsInfo().containsKey(carIndex)) {
            return;
        }
        sendRequest(AccBroadcastingProtocol.buildFocusRequest(
                model.getConnectionID(),
                carIndex,
                model.getSessionInfo().getActiveCameraSet(),
                model.getSessionInfo().getActiveCamera()
        ));
    }

    /**
     * Sends a request to change the current camera.
     *
     * @param camSet The camera set to change to.
     * @param cam The specific camera to change to.
     */
    public void sendSetCameraRequest(String camSet, String cam) {
        sendRequest(AccBroadcastingProtocol.buildFocusRequest(
                model.getConnectionID(),
                model.getSessionInfo().getFocusedCarIndex(),
                camSet,
                cam
        ));
    }

    /**
     * Sends a request to change the current HUD page visible.
     *
     * @param page the hud page to change to.
     */
    public void sendSetHudPageRequest(String page) {
        sendRequest(AccBroadcastingProtocol.buildHudPageRequest(
                model.getConnectionID(),
                page
        ));
    }

    /**
     * Sends an instant replay request for the specified duration with focus on
     * the currently focused car and the current camera set and and camera
     *
     * @param seconds the ammont of seconds to replay back to.
     * @param duration the duration of the replay before returning to normal.
     */
    public void sendInstantReplayRequestSimple(float seconds, float duration) {
        sendRequest(AccBroadcastingProtocol.buildInstantReplayRequest(
                model.getConnectionID(),
                model.getSessionInfo().getSessionTime() - (seconds * 1000),
                duration * 1000,
                -1,
                "",
                ""
        ));
    }

    /**
     * Sends an instant replay request for the specified duration with focus on
     * the currently focused car and the current camera set and and camera
     *
     * @param sessionTime the time in the session to replay back to.
     * @param duration the duration of the replay before returning to normal.
     * @param carIndex the car to focus on in the replay
     * @param initialCameraSet the camera set to use.
     * @param initialCamera the camera to use.
     */
    public void sendInstantReplayRequestWithCamera(float sessionTime,
            float duration,
            int carIndex,
            String initialCameraSet,
            String initialCamera) {
        accListenerThread.setReplayCamera(carIndex, initialCameraSet, initialCamera);
        sendRequest(AccBroadcastingProtocol.buildInstantReplayRequest(
                model.getConnectionID(),
                sessionTime,
                duration * 1000,
                -1,
                "",
                ""
        ));
    }

    /**
     * Disconnect from the game.
     */
    public void disconnect() {
        socket.disconnect();
        socket.close();
    }

    private void sendRequest(byte[] requestBytes) {
        if (socket.isConnected()) {
            try {
                socket.send(new DatagramPacket(requestBytes, requestBytes.length));
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error sending request.", e);
            }
        }
    }

    public enum ExitState {
        NONE,
        NORMAL,
        REFUSED,
        PORT_UNREACHABLE,
        EXCEPTION,
        TIMEOUT
    };

    private class UdpListener
            extends Thread
            implements AccBroadcastingProtocolCallback {

        private final Logger LOG = Logger.getLogger(UdpListener.class.getName());

        /**
         * Protocoll used to communicate with ACC.
         */
        private final AccBroadcastingProtocol protocol = new AccBroadcastingProtocol(this);

        /**
         * exit state of this thread.
         */
        private ExitState exitState = ExitState.NONE;
        /**
         * flag to indicate that the socket was closed by the user.
         */
        private boolean forceExit = false;
        /**
         * Maps a car id to the ammount of missed realtime updates.
         */
        private final Map<Integer, Integer> missedRealtimeUpdates = new HashMap<>();
        /**
         * Ammount of missed realtime updates before disconnect.
         */
        private final int maximumRealtimeMisses = 5;
        /**
         * List of cars that have received a realtime update this tick.
         */
        private final List<Integer> realtimeUpdatesReceived = new ArrayList<>();
        /**
         * List of cars that have recentrly connected.
         */
        private final List<Integer> newConnectedCars = new ArrayList<>();
        /**
         * True means that the client is supposed to switch camera as soon as a
         * replay starts.
         */
        private boolean switchCameraForReplay = false;
        /**
         * Car to focus on when starting a replay.
         */
        private int replayCarId;
        /**
         * Camera set to use when starting a replay.
         */
        private String replayCameraSet;
        /**
         * Camera to use when starting a replay.
         */
        private String replayCamera;
        /**
         * If true the cameras will be reset when a replay is finished.
         */
        private boolean resetCameraWhenReplayIsDone = false;

        public UdpListener(String name) {
            super(name);
        }

        @Override
        public void run() {
            EventBus.publish(new ConnectionOpenedEvent());
            UILogger.log("Connection opened");
            LOG.info("Starting Listener thread");
            try {
                udpListener();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error in the listener thread", e);
                exitState = ExitState.EXCEPTION;
            } catch (StackOverflowError e) {
                LOG.log(Level.SEVERE, "Overflow in listener thread", e);
                exitState = ExitState.EXCEPTION;
            }
            EventBus.publish(new ConnectionClosedEvent(exitState));
            UILogger.log("Connection closed");
            LOG.info("Listener thread done");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            forceExit = true;
            socket.close();
        }

        public ExitState getExitState() {
            return exitState;
        }

        private void udpListener() {
            while (true) {
                try {
                    DatagramPacket response = new DatagramPacket(new byte[2048], 2048);
                    socket.receive(response);
                    protocol.processMessage(new ByteArrayInputStream(response.getData()));
                    afterPacketReceived(response.getData()[0]);
                } catch (SocketTimeoutException e) {
                    LOG.log(Level.WARNING, "Socket timed out.", e);
                    exitState = ExitState.TIMEOUT;
                    return;
                } catch (SocketException e) {
                    if (forceExit) {
                        LOG.info("Socket was closed by user.");
                        exitState = ExitState.NORMAL;
                    } else {
                        LOG.log(Level.SEVERE, "Socket closed unexpected.", e);
                        exitState = ExitState.PORT_UNREACHABLE;
                    }
                    return;
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error while receiving a response", e);
                    exitState = ExitState.EXCEPTION;
                    return;
                }
            }
        }

        @Override
        public void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message) {
            if (success == false) {
                LOG.info("Connection refused\n" + message);
                exitState = ExitState.REFUSED;
                stopAndKill();
                return;
            }
            model = model.withConnectionId(connectionID);

            try {
                sendEntryListRequest();
                sendTrackDataRequest();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while sending entrylist and trackdata request", e);
            }

            EventBus.publish(new RegistrationResultEvent(connectionID, success, readOnly, message));
        }

        @Override
        public void onRealtimeUpdate(SessionInfo sessionInfo) {
            SessionInfo oldInfo = model.getSessionInfo();
            model = model.withSessionInfo(sessionInfo);

            //Check for disconnected cars.
            checkForMissedRealtimeCarUpdates();

            //initialise sessionId.
            if (!sessionId.isValid()) {
                initSessionId(sessionInfo);
            }

            //update the current session.
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
                onSessionChanged(newSessionId, sessionInfo, false);
                sessionId = newSessionId;

                sessionPhase = SessionPhase.NONE;
            }
            //Fast forward to current phase
            while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChaged(sessionPhase, sessionInfo);
            }
            // find replay start and end
            boolean replayStarted = !oldInfo.isReplayPlaying() && sessionInfo.isReplayPlaying();
            boolean replayEnded = oldInfo.isReplayPlaying() && !sessionInfo.isReplayPlaying();

            if (replayStarted) {
                EventBus.publish(new ReplayStartedEvent());

                //set cameras when starting a replay with camera control.
                if (switchCameraForReplay) {
                    if (replayCarId != -1) {
                        sendChangeFocusRequest(replayCarId);
                    }
                    if (replayCameraSet != "" && replayCamera != "") {
                        sendSetCameraRequest(replayCameraSet, replayCamera);
                    }
                    switchCameraForReplay = false;
                    resetCameraWhenReplayIsDone = true;
                }
            }

            if (replayEnded) {
                EventBus.publish(new ReplayEndedEvent());

                //set cameras when a replay is done.
                if (resetCameraWhenReplayIsDone) {
                    sendSetCameraRequest(
                            model.getSessionInfo().getActiveCameraSet(),
                            model.getSessionInfo().getActiveCamera());
                    resetCameraWhenReplayIsDone = false;
                }
            }

            EventBus.publish(new RealtimeUpdateEvent(sessionInfo));
        }

        private void checkForMissedRealtimeCarUpdates() {
            //reset missed updates to 0 for cars that have received on.
            realtimeUpdatesReceived.forEach(carId -> missedRealtimeUpdates.put(carId, 0));

            //increase misses for cars that did not update
            model.getCarsInfo().values().stream()
                    .map(carInfo -> carInfo.getCarId())
                    .filter(carId -> !realtimeUpdatesReceived.contains(carId))
                    .forEach(carId -> {
                        missedRealtimeUpdates.put(carId, missedRealtimeUpdates.getOrDefault(carId, 0) + 1);
                    });

            realtimeUpdatesReceived.clear();

            //disconnect cars with excess of misses
            Iterator<Entry<Integer, Integer>> iter = missedRealtimeUpdates.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, Integer> entry = iter.next();
                if (entry.getValue() >= maximumRealtimeMisses) {
                    onCarDisconnect(model.getCar(entry.getKey()));
                    iter.remove();
                }
            }
        }

        private void initSessionId(SessionInfo sessionInfo) {
            SessionType type = sessionInfo.getSessionType();
            int sessionIndex = sessionInfo.getSessionIndex();
            int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
            sessionCounter.put(type, sessionNumber);

            SessionId newSessionId = new SessionId(type, sessionIndex, sessionNumber);
            onSessionChanged(newSessionId, sessionInfo, true);
            sessionId = newSessionId;

            sessionPhase = SessionPhase.NONE;
        }

        @Override
        public void onRealtimeCarUpdate(RealtimeInfo info) {
            //Update realtime misses to avoid disconnect.
            realtimeUpdatesReceived.add(info.getCarId());

            //update model
            if (model.getCarsInfo().containsKey(info.getCarId())) {
                CarInfo car = model.getCarsInfo().get(info.getCarId());
                car = car.withRealtime(info);

                Map<Integer, CarInfo> cars = new HashMap<>();
                cars.putAll(model.getCarsInfo());
                cars.put(car.getCarId(), car);
                model = model.withCars(cars);

                EventBus.publish(new RealtimeCarUpdateEvent(info));
            } else {
                //if the car doesnt exist in the model ask for a new entry list.
                long now = System.currentTimeMillis();
                if (now - lastTimeEntryListRequest > 5000) {
                    try {
                        sendEntryListRequest();
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Error while sending entrylist request", e);
                    }
                }
            }
        }

        @Override
        public void onEntryListUpdate(List<Integer> carIds) {
            Map<Integer, CarInfo> cars = new HashMap<>();
            cars.putAll(model.getCarsInfo());

            //add any new carIds.
            carIds.forEach(carId -> {
                if (!cars.containsKey(carId)) {
                    cars.put(carId, new CarInfo());
                    newConnectedCars.add(carId);
                }
            });
            model = model.withCars(cars);
            EventBus.publish(new EntryListUpdateEvent(carIds));
        }

        @Override
        public void onTrackData(TrackInfo info) {
            model = model.withTrackInfo(info);
            EventBus.publish(new TrackDataEvent(info));
        }

        @Override
        public void onEntryListCarUpdate(CarInfo carInfo) {
            //Fire Car connection event if the car is new.
            if (newConnectedCars.contains(carInfo.getCarId())) {
                onCarConnect(carInfo);
                newConnectedCars.remove(Integer.valueOf(carInfo.getCarId()));
            }
            EventBus.publish(new EntryListCarUpdateEvent(carInfo));
        }

        @Override
        public void onBroadcastingEvent(BroadcastingEvent event) {
            List<BroadcastingEvent> events = new LinkedList<>();
            events.addAll(model.getEvents());
            events.add(event);

            model = model.withEvents(events);
            EventBus.publish(new BroadcastingEventEvent(event));
        }

        @Override
        public void afterPacketReceived(byte type) {
            packetCount++;
            EventBus.publish(new AfterPacketReceivedEvent(type, packetCount));
        }

        private void onSessionChanged(SessionId newId, SessionInfo info, boolean init) {
            LOG.info("session changed to " + newId.getType().name() + " Index:" + newId.getIndex() + " sessionCount:" + newId.getNumber());
            EventBus.publish(new SessionChangedEvent(newId, info, init));
        }

        private void onSessionPhaseChaged(SessionPhase phase, SessionInfo info) {
            LOG.info("session phase changed to " + phase.name());
            //Create sessionInfo object with the correct sessionPhase
            SessionInfo correctedSessionInfo = new SessionInfo(info.getEventIndex(),
                    info.getSessionIndex(), info.getSessionType(), phase,
                    info.getSessionTime(), info.getSessionEndTime(), info.getFocusedCarIndex(),
                    info.getActiveCameraSet(), info.getActiveCamera(), info.getCurrentHudPage(),
                    info.isReplayPlaying(), info.getReplaySessionTime(), info.getReplayRemainingTime(),
                    info.getTimeOfDay(), info.getAmbientTemp(), info.getTrackTemp(),
                    info.getCloudLevel(), info.getRainLevel(), info.getWetness(),
                    info.getBestSessionLap());
            EventBus.publish(new SessionPhaseChangedEvent(correctedSessionInfo));
        }

        private void onCarDisconnect(CarInfo car) {
            //remove car from the model.
            Map<Integer, CarInfo> cars = new HashMap<>(model.getCarsInfo());
            cars.remove(car.getCarId());
            model = model.withCars(cars);

            String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
            LOG.info("Car disconnected: #" + car.getCarNumber() + "\t" + name);
            UILogger.log("Car disconnected: #" + car.getCarNumber() + "\t" + name);
            EventBus.publish(new CarDisconnectedEvent(car));
        }

        private void onCarConnect(CarInfo car) {
            //add car to the model.
            Map<Integer, CarInfo> cars = new HashMap<>(model.getCarsInfo());
            cars.put(car.getCarId(), car);
            model = model.withCars(cars);

            String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
            LOG.info("Car connected: #" + car.getCarNumber() + "\t" + name);
            UILogger.log("Car connected: #" + car.getCarNumber() + "\t" + name);
            EventBus.publish(new CarConnectedEvent(car));
        }

        /**
         * Sets the camera options to use for a replay.
         *
         * @param carId the car to focus on.
         * @param cameraSet the camera set to use.
         * @param camera the camera to use.
         */
        public void setReplayCamera(int carId, String cameraSet, String camera) {
            switchCameraForReplay = true;
            replayCarId = carId;
            replayCameraSet = cameraSet;
            replayCamera = camera;
        }
    }
}
