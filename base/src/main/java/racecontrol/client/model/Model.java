/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import racecontrol.client.protocol.SessionId;
import racecontrol.client.protocol.TrackInfo;
import racecontrol.client.protocol.enums.SessionType;

/**
 *
 * @author Leonard
 */
public class Model {

    /**
     * Address where the ACC broadcast server is running.
     */
    public InetAddress hostAddress = null;
    /**
     * Port where the ACC broadcast server is running.
     */
    public int hostPort;
    /**
     * Display name of this connection.
     */
    public String displayName = "";
    /**
     * Connection password.
     */
    public String connectionPassword = "";
    /**
     * Command password.
     */
    public String commandPassword = "";
    /**
     * Interval in which to receive updated in ms.
     */
    public int updateInterval;
    /**
     * Session id for the current session.
     */
    public SessionId currentSessionId = new SessionId(SessionType.NONE, -1, 0);
    /**
     * The connection id.
     */
    public int connectionId = -1;
    /**
     * Flag for when the connection is in read only mode.
     */
    public boolean readOnly;
    /**
     * Whether the game is connected or not.
     */
    public boolean gameConnected = false;
    /**
     * Track information.
     */
    public TrackInfo trackInfo;
    /**
     * Current session.
     */
    public Session session = new Session();
    /**
     * Collection of all cars for this event.
     */
    public Map<Integer, Car> cars = new HashMap<>();

    /**
     * Creates a deep copy of the model.
     *
     * @return a deep copy of this model.
     */
    public synchronized Model copy() {
        Model model = new Model();
        model.hostAddress = hostAddress;
        model.hostPort = hostPort;
        model.displayName = displayName;
        model.connectionPassword = connectionPassword;
        model.commandPassword = commandPassword;
        model.updateInterval = updateInterval;
        model.currentSessionId = currentSessionId;
        model.connectionId = connectionId;
        model.readOnly = readOnly;
        model.gameConnected = gameConnected;
        model.trackInfo = trackInfo;
        model.session = session.copy();
        model.cars = new HashMap<>();
        cars.forEach((id, car) -> model.cars.put(id, car.copy()));
        return model;
    }
}
