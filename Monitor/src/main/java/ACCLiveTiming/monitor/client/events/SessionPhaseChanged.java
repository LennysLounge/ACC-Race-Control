/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.client.events;

import ACCLiveTiming.monitor.eventbus.Event;
import ACCLiveTiming.monitor.networking.data.SessionInfo;

/**
 *
 * @author Leonard
 */
public class SessionPhaseChanged extends Event {

    private SessionInfo sessionInfo;

    public SessionPhaseChanged(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
