/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.client.events;

import acclivetiming.Monitor.eventbus.Event;
import acclivetiming.Monitor.networking.data.SessionInfo;

/**
 *
 * @author Leonard
 */
public class RealtimeUpdate extends Event {

    private SessionInfo sessionInfo;

    public RealtimeUpdate(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
