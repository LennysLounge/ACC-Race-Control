/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.networking.events.AfterPacketReceived;
import base.screen.networking.events.CarConnect;
import base.screen.networking.events.CarDisconnect;
import base.screen.networking.events.EntryListCarUpdate;
import base.screen.networking.events.RealtimeCarUpdate;
import base.screen.networking.events.RegistrationResult;
import base.screen.networking.events.SessionPhaseChanged;
import base.screen.networking.events.TrackData;
import base.screen.eventbus.EventBus;
import base.screen.extensions.logging.LoggingExtension;
import base.screen.networking.data.AccBroadcastingData;
import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.data.TrackInfo;
import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;
import base.screen.networking.events.ConnectionClosed;
import base.screen.networking.events.ConnectionOpened;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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

/**
 * A basic connection to the broadcasting interface from Assetto Corsa
 * Competizione.
 *
 * @author Leonard
 */
public class AccBroadcastingClient {

    private static final Logger LOG = Logger.getLogger(AccBroadcastingClient.class.getName());

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
     * Default contructor.
     */
    public AccBroadcastingClient() {
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

        socket = new DatagramSocket();
        socket.connect(this.hostAddress, this.hostPort);

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
                && accListenerThread.isAlive()) {
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
     * Sends an instant replay request for the specified duration with focus
     * on the currently focused car and the current camera set and and camera
     *
     * @param seconds the ammont of seconds to replay back to.
     * @param duration the duration of the replay before returning to normal.
     */
    public void sendInstantReplayRequest(float seconds, float duration ) {
        sendRequest(AccBroadcastingProtocol.buildInstantReplayRequest(
                model.getConnectionID(),
                model.getSessionInfo().getSessionTime()-(seconds*1000),
                duration*1000,
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
        NORMAL,
        REFUSED,
        PORT_UNREACHABLE,
        EXCEPTION
    };

    private class UdpListener
            extends Thread
            implements AccBroadcastingClientListener {

        private final Logger LOG = Logger.getLogger(UdpListener.class.getName());

        /**
         * Protocoll used to communicate with ACC.
         */
        private final AccBroadcastingProtocol protocol = new AccBroadcastingProtocol(this);

        /**
         * exit state of this thread.
         */
        private ExitState exitState = ExitState.NORMAL;
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

        public UdpListener(String name) {
            super(name);
        }

        @Override
        public void run() {
            EventBus.publish(new ConnectionOpened());
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
            EventBus.publish(new ConnectionClosed(exitState));
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
                    DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(response);
                    protocol.processMessage(new ByteArrayInputStream(response.getData()));
                    afterPacketReceived(response.getData()[0]);
                } catch (SocketException e) {
                    if (forceExit) {
                        LOG.info("Socket was closed by user.");
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

            EventBus.publish(new RegistrationResult(connectionID, success, readOnly, message));
        }

        @Override
        public void onRealtimeUpdate(SessionInfo sessionInfo) {
            model = model.withSessionInfo(sessionInfo);

            //Check for disconnected cars.
            checkForMissedRealtimeUpdates();

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

        private void checkForMissedRealtimeUpdates() {
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

        @Override
        public void onRealtimeCarUpdate(RealtimeInfo info
        ) {
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

                EventBus.publish(new RealtimeCarUpdate(info));
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
        public void onEntryListUpdate(List<Integer> carIds
        ) {
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
            EventBus.publish(new EntryListUpdate(carIds));
        }

        @Override
        public void onTrackData(TrackInfo info
        ) {
            model = model.withTrackInfo(info);
            EventBus.publish(new TrackData(info));
        }

        @Override
        public void onEntryListCarUpdate(CarInfo carInfo
        ) {
            //Fire Car connection event if the car is new.
            if (newConnectedCars.contains(carInfo.getCarId())) {
                onCarConnect(carInfo);
                newConnectedCars.remove(Integer.valueOf(carInfo.getCarId()));
            }
            EventBus.publish(new EntryListCarUpdate(carInfo));
        }

        @Override
        public void onBroadcastingEvent(BroadcastingEvent event
        ) {
            List<BroadcastingEvent> events = new LinkedList<>();
            events.addAll(model.getEvents());
            events.add(event);

            model = model.withEvents(events);
            EventBus.publish(new BroadcastingEventEvent(event));

        }

        @Override
        public void afterPacketReceived(byte type
        ) {
            packetCount++;
            EventBus.publish(new AfterPacketReceived(type, packetCount));
        }

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
            //remove car from the model.
            Map<Integer, CarInfo> cars = new HashMap<>(model.getCarsInfo());
            cars.remove(car.getCarId());
            model = model.withCars(cars);

            String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
            LoggingExtension.log("Car disconnected: #" + car.getCarNumber() + "\t" + name);
            LOG.info("Car disconnected: #" + car.getCarNumber() + "\t" + name);
            EventBus.publish(new CarDisconnect(car));
        }

        private void onCarConnect(CarInfo car) {
            //add car to the model.
            Map<Integer, CarInfo> cars = new HashMap<>(model.getCarsInfo());
            cars.put(car.getCarId(), car);
            model = model.withCars(cars);

            String name = car.getDriver().getFirstName() + " " + car.getDriver().getLastName();
            LoggingExtension.log("Car connected: #" + car.getCarNumber() + "\t" + name);
            LOG.info("Car connected: #" + car.getCarNumber() + "\t" + name);
            EventBus.publish(new CarConnect(car));
        }
    }
}
