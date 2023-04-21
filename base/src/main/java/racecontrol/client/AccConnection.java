/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client;

import racecontrol.client.protocol.AccBroadcastingProtocolCallback;
import racecontrol.client.protocol.AccBroadcastingProtocol;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import racecontrol.Main;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.BroadcastingEvent;
import racecontrol.client.protocol.CarInfo;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.SessionId;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.protocol.TrackInfo;
import racecontrol.client.protocol.enums.SessionPhase;
import racecontrol.client.protocol.enums.SessionType;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.CarDisconnectedEvent;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.EntryListCarUpdateEvent;
import racecontrol.client.events.EntryListUpdateEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.RegistrationResultEvent;
import racecontrol.client.events.ReplayEndedEvent;
import racecontrol.client.events.ReplayStartedEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.client.model.Car;
import racecontrol.client.model.Driver;
import racecontrol.client.model.Model;
import racecontrol.client.protocol.enums.Nationality;
import racecontrol.eventbus.EventBus;
import racecontrol.logging.UILogger;

/**
 *
 * @author Leonard
 */
public class AccConnection
        extends Thread
        implements AccBroadcastingProtocolCallback {

    private final Logger LOG = Logger.getLogger(AccConnection.class.getName());

    /**
     * exit state of this thread.
     */
    private ExitState exitState = ExitState.NONE;
    /**
     * flag to indicate that the socket was closed by the user.
     */
    private boolean forceExit = false;
    /**
     * Flag to indicate that the connection should be running.
     */
    private boolean running = true;
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
    /**
     * Current Phase of the session.
     */
    private SessionPhase sessionPhase = SessionPhase.NONE;
    /**
     * Counter coutns how many of a session have happened.
     */
    private final Map<SessionType, Integer> sessionCounter = new HashMap<>();
    /**
     * Time when the entry list was requested.
     */
    private long lastTimeEntryListRequest = 0;
    /**
     * The model.
     */
    private Model model = new Model();
    /**
     * Socket used for the connection.
     */
    private final DatagramSocket socket;

    public AccConnection(Model model) throws SocketException {
        super("ACC connection thread");
        this.model = model;

        Thread.setDefaultUncaughtExceptionHandler(new Main.UncoughtExceptionHandler());

        //create socket
        socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        socket.connect(model.hostAddress, model.hostPort);
    }

    @Override
    public void run() {
        EventBus.publish(new ConnectionOpenedEvent());
        UILogger.log("Connection opened");
        LOG.info("Starting connection thread");

        // register to the game.
        sendRequest(AccBroadcastingProtocol.buildRegisterRequest(
                model.displayName,
                model.connectionPassword,
                model.updateInterval,
                model.commandPassword));

        // start listener loop.
        while (running) {
            try {
                DatagramPacket response = new DatagramPacket(new byte[2048], 2048);
                socket.receive(response);
                AccBroadcastingProtocol.processMessage(
                        new ByteArrayInputStream(response.getData()), this);
                afterPacketReceived(response.getData()[0]);
            } catch (SocketTimeoutException e) {
                LOG.log(Level.WARNING, "Socket timed out.", e);
                exitState = ExitState.TIMEOUT;
                running = false;
            } catch (PortUnreachableException e) {
                LOG.log(Level.SEVERE, "Socket is unreachable", e);
                exitState = ExitState.PORT_UNREACHABLE;
                running = false;
            } catch (SocketException e) {
                if (forceExit) {
                    LOG.info("Socket was closed by user.");
                    exitState = ExitState.USER;
                } else {
                    LOG.log(Level.SEVERE, "Socket closed unexpected.", e);
                    exitState = ExitState.EXCEPTION;
                }
                running = false;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error in the listener thread", e);
                exitState = ExitState.EXCEPTION;
                running = false;
            } catch (StackOverflowError e) {
                LOG.log(Level.SEVERE, "Overflow in listener thread", e);
                exitState = ExitState.EXCEPTION;
                running = false;
            }
        }

        // set game to disconnected
        model.gameConnected = false;

        // send close connection
        EventBus.publish(new ConnectionClosedEvent(exitState));
        UILogger.log("Connection closed");
        LOG.info("Connection thread done");
    }

    /**
     * Interrupts the connection to initiate it to close.
     */
    public void close() {
        super.interrupt();
        forceExit = true;
        socket.close();
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
                && isAlive()
                && running) {
            return true;
        }
        return false;
    }

    /**
     * Send a request to the game.
     *
     * @param requestBytes
     */
    public void sendRequest(byte[] requestBytes) {
        if (socket.isConnected()) {
            try {
                socket.send(new DatagramPacket(requestBytes, requestBytes.length));
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error sending request.", e);
            }
        }
    }

    /**
     * Send a register request.
     */
    public void sendRegisterRequest() {
        if (!isConnected()) {
            return;
        }
        sendRequest(AccBroadcastingProtocol.buildRegisterRequest(
                model.displayName,
                model.connectionPassword,
                model.updateInterval,
                model.commandPassword
        ));
    }

    /**
     * Send unregister request.
     */
    public void sendUnregisterRequest() {
        if (!isConnected()) {
            return;
        }
        sendRequest(AccBroadcastingProtocol.buildUnregisterRequest(model.connectionId));
    }

    /**
     * Send a request for the current entry list.
     */
    public void sendEntryListRequest() {
        if (!isConnected()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastTimeEntryListRequest > 5000) {
            lastTimeEntryListRequest = now;
            sendRequest(AccBroadcastingProtocol.buildEntryListRequest(model.connectionId));
        }
    }

    /**
     * Send a request for the current track data.
     */
    public void sendTrackDataRequest() {
        if (!isConnected()) {
            return;
        }
        sendRequest(AccBroadcastingProtocol.buildTrackDataRequest(model.connectionId));
    }

    //
    //                      Broadcasting callbacks.
    //
    @Override
    public void onRegistrationResult(int connectionId, boolean success, boolean readOnly, String message) {
        if (success == false) {
            LOG.info("Connection refused\n" + message);
            exitState = ExitState.REFUSED;
            running = false;
            return;
        }

        model.connectionId = connectionId;
        model.readOnly = readOnly;
        model.gameConnected = true;

        sendEntryListRequest();
        sendTrackDataRequest();

        EventBus.publish(new RegistrationResultEvent(connectionId, success, readOnly, message));
    }

    @Override
    public void onRealtimeUpdate(SessionInfo sessionInfo) {
        SessionInfo oldInfo = model.session.raw;
        model.session.raw = sessionInfo;

        checkForDisconnects();

        //initialise sessionId.
        if (!model.currentSessionId.isValid()) {
            initSessionId(sessionInfo);
            // fast forward to correct phase
            while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChaged(sessionPhase, sessionInfo, true);
            }
        }

        //update the current session.
        if (model.currentSessionId.getIndex() != sessionInfo.getSessionIndex()) {
            //fast forward currnet session to result UI
            while (sessionPhase != SessionPhase.RESULTUI) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChaged(sessionPhase, sessionInfo, false);
            }
            //Move to next sessionId;
            SessionType type = sessionInfo.getSessionType();
            int sessionIndex = sessionInfo.getSessionIndex();
            int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
            sessionCounter.put(type, sessionNumber);

            SessionId newSessionId = new SessionId(type, sessionIndex, sessionNumber);
            onSessionChanged(newSessionId, sessionInfo, false);
            model.currentSessionId = newSessionId;

            sessionPhase = SessionPhase.NONE;
        }
        //Fast forward to current phase
        while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
            sessionPhase = SessionPhase.getNext(sessionPhase);
            onSessionPhaseChaged(sessionPhase, sessionInfo, false);
        }
        // find replay start and end
        boolean replayStarted = !oldInfo.isReplayPlaying() && sessionInfo.isReplayPlaying();
        boolean replayEnded = oldInfo.isReplayPlaying() && !sessionInfo.isReplayPlaying();

        if (replayStarted) {
            EventBus.publish(new ReplayStartedEvent());

            //set cameras when starting a replay with camera control.
            if (switchCameraForReplay) {
                if (replayCarId != -1) {
                    getClient().sendChangeFocusRequest(replayCarId);
                }
                if (replayCameraSet != "" && replayCamera != "") {
                    getClient().sendSetCameraRequest(replayCameraSet, replayCamera);
                }
                switchCameraForReplay = false;
                resetCameraWhenReplayIsDone = true;
            }
        }

        if (replayEnded) {
            EventBus.publish(new ReplayEndedEvent());

            //set cameras when a replay is done.
            if (resetCameraWhenReplayIsDone) {
                getClient().sendSetCameraRequest(model.session.raw.getActiveCameraSet(),
                        model.session.raw.getActiveCamera());
                resetCameraWhenReplayIsDone = false;
            }
        }
        EventBus.publish(new RealtimeUpdateEvent(sessionInfo));
    }

    private void checkForDisconnects() {
        long now = System.currentTimeMillis();
        model.getCars().forEach(car -> {
            if (car.connected) {
                if (now - car.lastUpdate > model.updateInterval * 10) {
                    car.connected = false;

                    String name = car.getDriver().fullName();
                    LOG.info("Car disconnected: " + car.carNumberString() + "\t" + name);
                    UILogger.log("Car disconnected: " + car.carNumberString() + "\t" + name);
                    EventBus.publish(new CarDisconnectedEvent(car));
                }
            }
        });
    }

    private void initSessionId(SessionInfo sessionInfo) {
        SessionType type = sessionInfo.getSessionType();
        int sessionIndex = sessionInfo.getSessionIndex();
        int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
        sessionCounter.put(type, sessionNumber);

        SessionId newSessionId = new SessionId(type, sessionIndex, sessionNumber);
        onSessionChanged(newSessionId, sessionInfo, true);
        model.currentSessionId = newSessionId;

        sessionPhase = SessionPhase.NONE;
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        var carOpt = model.getCar(info.getCarId());
        carOpt.ifPresentOrElse(car -> {
            car.lastUpdate = System.currentTimeMillis();
            car.connected = true;
            car.driverIndexRealtime = info.getDriverIndex();
            car.driverCount = info.getDriverCount();
            car.gear = info.getGear();
            car.yaw = info.getYaw();
            car.pitch = info.getPitch();
            car.roll = info.getRoll();
            car.carLocation = info.getLocation();
            car.KMH = info.getKMH();
            car.position = info.getPosition();
            car.cupPosition = info.getCupPosition();
            car.trackPosition = info.getTrackPosition();
            car.splinePosition = info.getSplinePosition();
            car.lapCount = info.getLaps();
            car.delta = info.getDelta();
            car.bestLap = info.getBestSessionLap();
            car.lastLap = info.getLastLap();
            car.currentLap = info.getCurrentLap();
            EventBus.publish(new RealtimeCarUpdateEvent(info));
        }, () -> {
            //if the car doesnt exist in the model ask for a new entry list.
            sendEntryListRequest();
        });
    }

    @Override
    public void onEntryListUpdate(List<Integer> carIds) {
        EventBus.publish(new EntryListUpdateEvent(carIds));
    }

    @Override
    public void onTrackData(TrackInfo info) {
        model.trackInfo = info;
        EventBus.publish(new TrackInfoEvent(info));
    }

    @Override
    public void onEntryListCarUpdate(CarInfo carInfo) {
        Car car = model.getCar(carInfo.getCarId()).orElse(new Car());

        boolean newConnection = car.connected == false;

        car.lastUpdate = System.currentTimeMillis();
        car.connected = true;
        car.id = carInfo.getCarId();
        car.carModel = carInfo.getCarModel();
        car.teamName = carInfo.getTeamName();
        car.carNumber = carInfo.getCarNumber();
        car.cupCategory = carInfo.getCupCatergory();
        car.driverIndex = carInfo.getCurrentDriverIndex();
        car.nationality = Nationality.fromId(carInfo.getCarNationality());
        car.drivers = carInfo.getDrivers().stream()
                .map(driverInfo -> {
                    var driver = new Driver();
                    driver.firstName = driverInfo.getFirstName();
                    driver.lastName = driverInfo.getLastName();
                    driver.shortName = driverInfo.getShortName();
                    driver.category = driverInfo.getCategory();
                    driver.nationality = driverInfo.getDriverNationality();
                    return driver;
                })
                .collect(Collectors.toList());
        model.putCar(car);

        //Fire Car connection event if the car is new.
        if (newConnection) {
            LOG.info("Car connected: " + car.carNumberString() + "\t" + car.getDriver().fullName());
            UILogger.log("Car connected: " + car.carNumberString() + "\t" + car.getDriver().fullName());
            EventBus.publish(new CarConnectedEvent(car));
        }

        EventBus.publish(new EntryListCarUpdateEvent(carInfo));
    }

    @Override
    public void onBroadcastingEvent(BroadcastingEvent event) {
        EventBus.publish(new BroadcastingEventEvent(event));
    }

    @Override
    public void afterPacketReceived(byte type) {
        EventBus.publish(new AfterPacketReceivedEvent(type, 0));
    }

    private void onSessionChanged(SessionId newId, SessionInfo info, boolean init) {
        LOG.info("session changed to " + newId.getType().name() + " Index:" + newId.getIndex() + " sessionCount:" + newId.getNumber());
        EventBus.publish(new SessionChangedEvent(newId, info, init));
    }

    private void onSessionPhaseChaged(SessionPhase phase, SessionInfo info, boolean init) {
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
        EventBus.publish(new SessionPhaseChangedEvent(correctedSessionInfo, init));
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

    public enum ExitState {
        NONE,
        USER,
        REFUSED,
        PORT_UNREACHABLE,
        EXCEPTION,
        TIMEOUT
    };
}
