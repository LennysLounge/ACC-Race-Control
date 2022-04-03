/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.vsc.events;

import racecontrol.client.protocol.SessionId;
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
    private final int timeStamp;

    public VSCStartEvent(int speedLimit, int speedTolerance, int timeTolerance, SessionId sessionId, int timeStamp) {
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

    public int getTimeStamp() {
        return timeStamp;
    }
    
    
}
