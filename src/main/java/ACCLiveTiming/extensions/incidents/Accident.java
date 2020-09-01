/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.client.SessionId;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class Accident {
    
    private static Logger LOG = Logger.getLogger(Accident.class.getName());

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
    private final List<Integer> cars;
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

    public Accident(float time, int carId, long timestamp, SessionId sessionID,
            int incidentNumber) {
        this(time, time, Arrays.asList(carId), timestamp, sessionID, incidentNumber);
    }
    

    private Accident(float earliestTime, float latestTime, List<Integer> cars,
            long timestamp, SessionId sessionID, int incidentNumber) {
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.cars = cars;
        this.timestamp = timestamp;
        this.sessionID = sessionID;
        this.incidentNumber = incidentNumber;
    }

    public Accident addCar(float time, int carId, long timestamp) {
        List<Integer> c = new LinkedList<>();
        c.addAll(cars);
        c.add(carId);
        return new Accident(earliestTime,
                time,
                c,
                timestamp,
                sessionID,
                incidentNumber);
    }

    public Accident withIncidentNumber(int incidentNumber) {
        return new Accident(earliestTime, latestTime, cars, timestamp, sessionID, incidentNumber);
    }

    public float getEarliestTime() {
        return earliestTime;
    }

    public float getLatestTime() {
        return latestTime;
    }

    public List<Integer> getCars() {
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
