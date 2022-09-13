/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.returntogarage;

import racecontrol.client.model.Car;
import racecontrol.client.protocol.SessionId;
import racecontrol.eventbus.Event;

/**
 * An event for when a car returns to garage.
 *
 * @author Leonard
 */
public class ReturnToGarageEvent
        extends Event {

    /**
     * The session when the event occured
     */
    private final SessionId sessionId;
    /**
     * The session time when the event occured
     */
    private final int sessionTime;
    /**
     * Replay time for the event.
     */
    private final int replayTime;
    /**
     * The car that returned to garage.
     */
    private final Car car;

    public ReturnToGarageEvent(SessionId sessionId,
            int sessionTime,
            int replayTime,
            Car car) {
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
        this.replayTime = replayTime;
        this.car = car;
    }

    /**
     * The session id when the event occured.
     *
     * @return The session id when the event occured.
     */
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * The session time when the event occured.
     *
     * @return The session time when the event occured.
     */
    public int getSessionTime() {
        return sessionTime;
    }

    /**
     * The replay time when the event occured.
     *
     * @return The replay time when the event occured.
     */
    public int getReplayTime() {
        return replayTime;
    }

    /**
     * The car that returned to garage.
     *
     * @return The car that returned to garage.
     */
    public Car getCar() {
        return car;
    }
}
