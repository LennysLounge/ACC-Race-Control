/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

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

    private Map<Integer, CarInfo> cars = new HashMap<>();
    private SessionInfo session = new SessionInfo();
    private TrackInfo trackInfo = new TrackInfo();
    private List<BroadcastingEvent> events = new LinkedList<>();

    public AccBroadcastingData() {
    }

    public AccBroadcastingData(Map<Integer, CarInfo> cars, SessionInfo session,
            TrackInfo trackInfo, List<BroadcastingEvent> events) {
        this.session = requireNonNull(session, "session");
        this.trackInfo = requireNonNull(trackInfo, "trackInfo");
        this.cars = requireNonNull(cars, "cars");
        this.events = requireNonNull(events, "events");
    }

    public Map<Integer, CarInfo> getCarsInfo() {
        return Collections.unmodifiableMap(cars);
    }

    public CarInfo getCar(int carId) {
        return cars.getOrDefault(carId, new CarInfo());
    }

    public AccBroadcastingData withCars(Map<Integer, CarInfo> cars) {
        return new AccBroadcastingData(cars, session, trackInfo, events);
    }

    public SessionInfo getSessionInfo() {
        return session;
    }

    public AccBroadcastingData withSessionInfo(SessionInfo session) {
        return new AccBroadcastingData(cars, session, trackInfo, events);
    }

    public TrackInfo getTrackInfo() {
        return trackInfo;
    }

    public AccBroadcastingData withTrackInfo(TrackInfo trackInfo) {
        return new AccBroadcastingData(cars, session, trackInfo, events);
    }

    public List<BroadcastingEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public AccBroadcastingData withEvents(List<BroadcastingEvent> events) {
        return new AccBroadcastingData(cars, session, trackInfo, events);
    }

}
