/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.SessionId;
import racecontrol.client.data.SessionInfo;

/**
 *
 * @author Leonard
 */
public class SessionChanged extends Event {

    private final SessionId sessionId;
    private final SessionInfo sessionInfo;
    private final boolean initialisation;

    public SessionChanged(SessionId sessionId,
            SessionInfo sessionInfo,
            boolean initialisation) {
        this.sessionId = sessionId;
        this.sessionInfo = sessionInfo;
        this.initialisation = initialisation;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
    
    public boolean isInitialisation(){
        return initialisation;
    }

}
