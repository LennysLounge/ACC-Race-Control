/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.client.events;

import ACCLiveTiming.monitor.client.SessionId;
import ACCLiveTiming.monitor.eventbus.Event;
import ACCLiveTiming.monitor.networking.data.SessionInfo;

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
