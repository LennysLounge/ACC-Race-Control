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
public class VSCEndEvent extends Event {

    private final SessionId sessionId;
    private final int sessionTime;

    public VSCEndEvent(SessionId sessionId, int sessionTime) {
        this.sessionId = sessionId;
        this.sessionTime = sessionTime;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public int getSessionTime() {
        return sessionTime;
    }

}
