/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
