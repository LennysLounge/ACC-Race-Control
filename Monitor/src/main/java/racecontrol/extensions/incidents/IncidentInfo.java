/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.client.data.SessionId;
import racecontrol.client.data.CarInfo;
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
     * The rough replay time for this incident.
     */
    private final int replayTime;
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

    public IncidentInfo(float time, int replayTime, CarInfo car, SessionId sessionId) {
        this(time,
                time,
                Arrays.asList(car),
                System.currentTimeMillis(),
                sessionId,
                replayTime
        );
    }

    public IncidentInfo(float time, int replayTime, SessionId sessionId) {
        this(time,
                time,
                new LinkedList<CarInfo>(),
                System.currentTimeMillis(),
                sessionId,
                replayTime
        );
    }

    private IncidentInfo(float earliestTime,
            float latestTime,
            List<CarInfo> cars,
            long timestamp,
            SessionId sessionID,
            int replayTime) {
        this.sessionEarliestTime = earliestTime;
        this.sessionLatestTime = latestTime;
        this.cars = cars;
        this.systemTimestamp = timestamp;
        this.sessionID = sessionID;
        this.replayTime = replayTime;
    }

    public IncidentInfo addCar(float time, CarInfo car, long timestamp) {
        List<CarInfo> c = new LinkedList<>();
        c.addAll(cars);
        c.add(car);
        return new IncidentInfo(sessionEarliestTime,
                time,
                c,
                timestamp,
                sessionID,
                replayTime);
    }

    public IncidentInfo withReplayTime(int replayTime) {
        return new IncidentInfo(sessionEarliestTime,
                sessionLatestTime,
                cars,
                systemTimestamp,
                sessionID,
                replayTime);
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

    public int getReplayTime() {
        return replayTime;
    }

}
