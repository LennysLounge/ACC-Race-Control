/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client;

import racecontrol.client.data.AccBroadcastingData;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Logger;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.extension.autobroadcast.AutobroadcastExtension;
import racecontrol.client.extension.contact.ContactExtension;
import racecontrol.client.extension.dangerdetection.DangerDetectionExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.laptimes.LapTimeExtension;
import racecontrol.client.extension.raceevent.RaceEventExtension;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.racereport.RaceReportExtension;
import racecontrol.client.extension.results.ResultsExtension;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.extension.trackdata.TrackDataExtension;
import racecontrol.client.extension.vsc.VirtualSafetyCarExtension;
import racecontrol.client.model.Model;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * A basic connection to the broadcasting interface from Assetto Corsa
 * Competizione.
 *
 * @author Leonard
 */
public class AccBroadcastingClient
        implements EventListener {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(AccBroadcastingClient.class.getName());
    /**
     * Singelton instance.
     */
    private static AccBroadcastingClient instance;
    /**
     * The model.
     */
    private Model model = new Model();
    /**
     * Thread where the connection loop is running.
     */
    private AccConnection connection;
    /**
     * List of broadcast extensions.
     */
    private final List<ClientExtension> extensions = new ArrayList<>();

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

    private AccBroadcastingClient() {
        EventBus.register(this);
    }

    public void initialise() {
        //instanciate extensions
        extensions.add(ContactExtension.getInstance());
        extensions.add(GoogleSheetsAPIExtension.getInstance());
        extensions.add(new LapTimeExtension());
        extensions.add(RaceReportExtension.get());
        extensions.add(ReplayOffsetExtension.getInstance());
        extensions.add(new ResultsExtension());
        extensions.add(TrackDataExtension.getInstance());
        extensions.add(DangerDetectionExtension.getInstance());
        extensions.add(VirtualSafetyCarExtension.getInstance());
        extensions.add(VirtualSafetyCarExtension.getInstance());
        extensions.add(RaceEventExtension.get());
        extensions.add(AutobroadcastExtension.getInstance());

        // statistics should always go last.
        extensions.add(StatisticsExtension.getInstance());
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionClosedEvent) {
            connection = null;
        }
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
        if (connection != null) {
            return;
        }

        model = new Model();
        model.displayName = requireNonNull(displayName, "displayName");
        model.connectionPassword = requireNonNull(connectionPassword, "connectionPassword");
        model.commandPassword = requireNonNull(commandPassword, "commandPassword");
        if (updateInterval < 0) {
            throw new IllegalArgumentException("Update interval cannot be less than 0");
        }
        model.updateInterval = updateInterval;
        model.hostAddress = requireNonNull(hostAddress, "hostAddress");
        model.hostPort = requireNonNull(hostPort, "hostPort");

        extensions.forEach(extension -> extension.setWritableModel(model));

        connection = new AccConnection(model);
        connection.start();
    }

    public void stopAndKill() {
        if (connection == null) {
            return;
        }
        LOG.info("Stopping connection thread");
        connection.close();
    }

    /**
     * Returns the current model.
     *
     * @return The current model.
     */
    public Model getModel() {
        return model.copy();
    }

    /**
     * Returns the current model.
     *
     * @return The current data model.
     */
    public AccBroadcastingData getBroadcastingData() {
        if (connection != null) {
            return connection.getBroadcastingData();
        }
        return new AccBroadcastingData();
    }

    /**
     * Returns true if the socket is connected and currently listening for a
     * packet.
     *
     * @return True when connected.
     */
    public boolean isConnected() {
        if (connection != null) {
            return connection.isConnected();
        }
        return false;
    }

    /**
     * Send unregister request.
     */
    public void sendUnregisterRequest() {
        if (connection != null) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildUnregisterRequest(model.connectionId));
    }

    /**
     * Send a request to change the currently focused car.
     *
     * @param carIndex the car index of the car to focus on.
     */
    public void sendChangeFocusRequest(int carIndex) {
        if (connection == null) {
            return;
        }
        if (!model.cars.containsKey(carIndex)) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildFocusRequest(model.connectionId,
                        carIndex,
                        model.session.raw.getActiveCameraSet(),
                        model.session.raw.getActiveCamera()
                ));
    }

    /**
     * Sends a request to change the current camera.
     *
     * @param camSet The camera set to change to.
     * @param cam The specific camera to change to.
     */
    public void sendSetCameraRequest(String camSet, String cam) {
        if (connection == null) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildFocusRequest(model.connectionId,
                        model.session.raw.getFocusedCarIndex(),
                        camSet,
                        cam
                ));
    }

    /**
     * Sends a request to change the current camera and the focus.
     *
     * @param carIndex the car index of the car to focus on.
     * @param camSet The camera set to change to.
     * @param cam The specific camera to change to.
     */
    public void sendSetCameraRequestWithFocus(int carIndex, String camSet, String cam) {
        if (connection == null) {
            return;
        }
        if (!model.cars.containsKey(carIndex)) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildFocusRequest(model.connectionId,
                        carIndex,
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
        if (connection == null) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildHudPageRequest(model.connectionId,
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
        if (connection == null) {
            return;
        }
        connection.sendRequest(AccBroadcastingProtocol
                .buildInstantReplayRequest(model.connectionId,
                        model.session.raw.getSessionTime() - (seconds * 1000),
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
        if (connection == null) {
            return;
        }
        connection.setReplayCamera(carIndex, initialCameraSet, initialCamera);
        connection.sendRequest(AccBroadcastingProtocol
                .buildInstantReplayRequest(model.connectionId,
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
        if (connection == null) {
            return;
        }
        connection.close();
    }
}
