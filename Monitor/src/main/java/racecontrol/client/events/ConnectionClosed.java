/*
 * Copyright (c) 2021 Leonard Sch�ngel
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
