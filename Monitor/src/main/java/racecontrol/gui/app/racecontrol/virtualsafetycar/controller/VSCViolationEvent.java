/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.virtualsafetycar.controller;

import racecontrol.client.data.SessionId;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class VSCViolationEvent extends Event{
    
    private final int carId;
    private final int speedOver;
    private final int timeOver;
    private final SessionId sessionId;
    private final int sessionTime;

    public VSCViolationEvent(int carId, int speedOver, int time, SessionId sessionId, int sessionTime) {
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

    public int getTimeOver() {
        return timeOver;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public int getSessionTime() {
        return sessionTime;
    }
}
