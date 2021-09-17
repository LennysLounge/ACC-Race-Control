/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.virtualsafetycar.controller;

import racecontrol.client.data.SessionId;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class VSCViolationEvent extends Event{
    
    private final int carId;
    private final int speedOver;
    private final float timeOver;
    private final SessionId sessionId;
    private final float sessionTime;

    public VSCViolationEvent(int carId, int speedOver, float time, SessionId sessionId, float sessionTime) {
        this.carId = carId;
        this.speedOver = speedOver;
        this.timeOver = time;
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
    }

    public int getCarId() {
        return carId;
    }

    public int getSpeedOver() {
        return speedOver;
    }

    public float getTimeOver() {
        return timeOver;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public float getSessionTime() {
        return sessionTime;
    }
}
