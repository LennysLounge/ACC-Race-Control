/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.networking.SessionId;
import base.screen.eventbus.Event;
import base.screen.networking.data.SessionInfo;

/**
 *
 * @author Leonard
 */
public class SessionChanged extends Event {

    private SessionId sessionId;
    private SessionInfo sessionInfo;

    public SessionChanged(SessionId sessionId, SessionInfo sessionInfo) {
        this.sessionId = sessionId;
        this.sessionInfo = sessionInfo;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
