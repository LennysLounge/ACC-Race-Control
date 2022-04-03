/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.protocol.SessionInfo;

/**
 *
 * @author Leonard
 */
public class SessionPhaseChangedEvent extends Event {

    private final SessionInfo sessionInfo;

    private final boolean initialisation;

    public SessionPhaseChangedEvent(SessionInfo sessionInfo, boolean init) {
        this.sessionInfo = sessionInfo;
        this.initialisation = init;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public boolean isInitialisation() {
        return initialisation;
    }

}
