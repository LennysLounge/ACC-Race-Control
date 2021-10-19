/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.AccBroadcastingClient.ExitState;

/**
 *
 * @author Leonard
 */
public class ConnectionClosedEvent
        extends Event {

    private final ExitState exitState;

    public ConnectionClosedEvent(ExitState exitState) {
        this.exitState = exitState;
    }

    public ExitState getExitState() {
        return exitState;
    }

}
