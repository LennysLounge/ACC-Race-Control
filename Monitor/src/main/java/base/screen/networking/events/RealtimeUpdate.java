/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.eventbus.Event;
import base.screen.networking.data.SessionInfo;

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
