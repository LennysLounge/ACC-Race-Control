/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents;

import base.screen.networking.SessionId;
import base.screen.networking.data.CarInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class IncidentInfo {

    private static Logger LOG = Logger.getLogger(IncidentInfo.class.getName());

    /**
     * time of the earliest accident event.
     */
    private final float sessionEarliestTime;
    /**
     * time of the latest accident event.
     */
    private final float sessionLatestTime;
    /**
     * List of cars involved by carID.
     */
    private final List<CarInfo> cars;
    /**
     * System timestamp for this accident.
     */
    private final long systemTimestamp;
    /**
     * The session index when it occured.
     */
    private final SessionId sessionID;

    public IncidentInfo(float time, CarInfo car, SessionId sessionID) {
        this(time, time, Arrays.asList(car), System.currentTimeMillis(), sessionID);
    }

    public IncidentInfo(float time, SessionId sessionId) {
        this(time, time, new LinkedList<CarInfo>(), System.currentTimeMillis(), sessionId);
    }

    private IncidentInfo(float earliestTime, float latestTime, List<CarInfo> cars,
            long timestamp, SessionId sessionID) {
        this.sessionEarliestTime = earliestTime;
        this.sessionLatestTime = latestTime;
        this.cars = cars;
        this.systemTimestamp = timestamp;
        this.sessionID = sessionID;
    }

    public IncidentInfo addCar(float time, CarInfo car, long timestamp) {
        List<CarInfo> c = new LinkedList<>();
        c.addAll(cars);
        c.add(car);
        return new IncidentInfo(sessionEarliestTime,
                time,
                c,
                timestamp,
                sessionID);
    }

    public float getSessionEarliestTime() {
        return sessionEarliestTime;
    }

    public float getSessionLatestTime() {
        return sessionLatestTime;
    }

    public List<CarInfo> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public long getSystemTimestamp() {
        return systemTimestamp;
    }

    public SessionId getSessionID() {
        return sessionID;
    }

}
