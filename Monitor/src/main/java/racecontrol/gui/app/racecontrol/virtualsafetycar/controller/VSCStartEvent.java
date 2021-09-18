/*
 * Copyright (c) 2021 Leonard Sch?ngel
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
public class VSCStartEvent extends Event{
    
    private final int speedLimit;
    private final int speedTolerance;
    private final int timeTolerance;
    private final SessionId sessionId;
    private final float timeStamp;

    public VSCStartEvent(int speedLimit, int speedTolerance, int timeTolerance, SessionId sessionId, float timeStamp) {
        this.speedLimit = speedLimit;
        this.speedTolerance = speedTolerance;
        this.timeTolerance = timeTolerance;
        this.sessionId = sessionId;
        this.timeStamp = timeStamp;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public int getSpeedTolerance() {
        return speedTolerance;
    }

    public int getTimeTolerance() {
        return timeTolerance;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public float getTimeStamp() {
        return timeStamp;
    }
    
    
}
