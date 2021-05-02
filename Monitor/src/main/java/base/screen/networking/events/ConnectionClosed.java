/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.AccBroadcastingClient.ExitState;

/**
 *
 * @author Leonard
 */
public class ConnectionClosed
        extends Event {

    private final ExitState exitState;

    public ConnectionClosed(ExitState exitState) {
        this.exitState = exitState;
    }

    public ExitState getExitState() {
        return exitState;
    }

}
