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
public class VSCEndEvent extends Event {

    private final SessionId sessionId;
    private final float sessionTime;

    public VSCEndEvent(SessionId sessionId, float sessionTime) {
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public float getSessionTime() {
        return sessionTime;
    }

}
