/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.SessionInfo;

/**
 *
 * @author Leonard
 */
public class SessionPhaseChangedEvent extends Event {

    private SessionInfo sessionInfo;

    public SessionPhaseChangedEvent(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
