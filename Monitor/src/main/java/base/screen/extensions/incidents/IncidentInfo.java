/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private final float earliestTime;
    /**
     * time of the latest accident event.
     */
    private final float latestTime;
    /**
     * List of cars involved by carID.
     */
    private final List<CarInfo> cars;
    /**
     * System timestamp for this accident.
     */
    private final long timestamp;
    /**
     * The session index when it occured.
     */
    private final SessionId sessionID;
    /**
     * The number of this accident.
     */
    private final int incidentNumber;

    public IncidentInfo(float time, CarInfo car, SessionId sessionID) {
        this(time, time, Arrays.asList(car), System.currentTimeMillis(), sessionID, 0);
    }

    public IncidentInfo(float time, SessionId sessionId) {
        this(time, time, new LinkedList<CarInfo>(), System.currentTimeMillis(), sessionId, 0);
    }

    private IncidentInfo(float earliestTime, float latestTime, List<CarInfo> cars,
            long timestamp, SessionId sessionID, int incidentNumber) {
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.cars = cars;
        this.timestamp = timestamp;
        this.sessionID = sessionID;
        this.incidentNumber = incidentNumber;
    }

    public IncidentInfo addCar(float time, CarInfo car, long timestamp) {
        List<CarInfo> c = new LinkedList<>();
        c.addAll(cars);
        c.add(car);
        return new IncidentInfo(earliestTime,
                time,
                c,
                timestamp,
                sessionID,
                incidentNumber);
    }

    public IncidentInfo withIncidentNumber(int incidentNumber) {
        return new IncidentInfo(earliestTime,
                latestTime,
                cars,
                timestamp,
                sessionID,
                incidentNumber);
    }

    public float getEarliestTime() {
        return earliestTime;
    }

    public float getLatestTime() {
        return latestTime;
    }

    public List<CarInfo> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SessionId getSessionID() {
        return sessionID;
    }

    public int getIncidentNumber() {
        return incidentNumber;
    }

}
