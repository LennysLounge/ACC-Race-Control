/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class AccBroadcastingData {

    private static Logger LOG = Logger.getLogger(AccBroadcastingData.class.getName());

    private int connectionID = -1;
    private boolean readOnly;
    private Map<Integer, CarInfo> cars = new HashMap<>();
    private SessionInfo session = new SessionInfo();
    private TrackInfo trackInfo = new TrackInfo();
    private List<BroadcastingEvent> events = new LinkedList<>();

    public AccBroadcastingData() {
    }

    public AccBroadcastingData(int connectionID, boolean readOnly, Map<Integer, CarInfo> cars, SessionInfo session,
            TrackInfo trackInfo, List<BroadcastingEvent> events) {
        this.connectionID = requireNonNull(connectionID, "connectionID");
        this.readOnly = requireNonNull(readOnly, "readOnly");
        this.session = requireNonNull(session, "session");
        this.trackInfo = requireNonNull(trackInfo, "trackInfo");
        this.cars = requireNonNull(cars, "cars");
        this.events = requireNonNull(events, "events");
    }

    public int getConnectionID() {
        return connectionID;
    }

    public AccBroadcastingData withConnectionId(int connectionID) {
        return new AccBroadcastingData(connectionID, readOnly, cars, session, trackInfo, events);
    }

    public Map<Integer, CarInfo> getCarsInfo() {
        return Collections.unmodifiableMap(cars);
    }

    public CarInfo getCar(int carId) {
        return cars.getOrDefault(carId, new CarInfo());
    }

    public AccBroadcastingData withCars(Map<Integer, CarInfo> cars) {
        return new AccBroadcastingData(connectionID, readOnly, cars, session, trackInfo, events);
    }

    public SessionInfo getSessionInfo() {
        return session;
    }

    public AccBroadcastingData withSessionInfo(SessionInfo session) {
        return new AccBroadcastingData(connectionID, readOnly, cars, session, trackInfo, events);
    }

    public TrackInfo getTrackInfo() {
        return trackInfo;
    }

    public AccBroadcastingData withTrackInfo(TrackInfo trackInfo) {
        return new AccBroadcastingData(connectionID, readOnly, cars, session, trackInfo, events);
    }

    public List<BroadcastingEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public AccBroadcastingData withEvents(List<BroadcastingEvent> events) {
        return new AccBroadcastingData(connectionID, readOnly, cars, session, trackInfo, events);
    }

}
