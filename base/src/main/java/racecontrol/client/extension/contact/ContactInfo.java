/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

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
public class ContactInfo {

    private static final Logger LOG = Logger.getLogger(ContactInfo.class.getName());

    /**
     * time of the earliest accident event.
     */
    private final int sessionEarliestTime;
    /**
     * time of the latest accident event.
     */
    private final int sessionLatestTime;
    /**
     * The rough replay time for this incident.
     */
    private final int replayTime;
    /**
     * List of cars involved by carID.
     */
    private final List<CarInfo> cars;
    /**
     * The session index when it occured.
     */
    private final SessionId sessionID;

    public ContactInfo(int time, int replayTime, CarInfo car, SessionId sessionId) {
        this(time,
                time,
                Arrays.asList(car),
                sessionId,
                replayTime
        );
    }

    public ContactInfo(int time, int replayTime, SessionId sessionId) {
        this(time,
                time,
                new LinkedList<CarInfo>(),
                sessionId,
                replayTime
        );
    }

    private ContactInfo(int earliestTime,
            int latestTime,
            List<CarInfo> cars,
            SessionId sessionID,
            int replayTime) {
        this.sessionEarliestTime = earliestTime;
        this.sessionLatestTime = latestTime;
        this.cars = cars;
        this.sessionID = sessionID;
        this.replayTime = replayTime;
    }

    public ContactInfo withCar(int sessionTime, CarInfo car) {
        List<CarInfo> c = new LinkedList<>();
        c.addAll(cars);
        c.add(car);
        return new ContactInfo(sessionEarliestTime,
                sessionTime,
                c,
                sessionID,
                replayTime);
    }

    public ContactInfo withReplayTime(int replayTime) {
        return new ContactInfo(sessionEarliestTime,
                sessionLatestTime,
                cars,
                sessionID,
                replayTime);
    }

    public int getSessionEarliestTime() {
        return sessionEarliestTime;
    }

    public int getSessionLatestTime() {
        return sessionLatestTime;
    }

    public List<CarInfo> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public SessionId getSessionID() {
        return sessionID;
    }

    public int getReplayTime() {
        return replayTime;
    }

}
