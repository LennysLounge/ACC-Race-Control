/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.returntogarage;

import racecontrol.eventbus.Event;

/**
 * An event for when the return to garage extension is enabled or disable.
 *
 * @author Leonard
 */
public class ReturnToGarageEnabledEvent
        extends Event {

    /**
     * The new state of the contact extension.
     */
    private final boolean newState;

    public ReturnToGarageEnabledEvent(boolean state) {
        this.newState = state;
    }

    public boolean getNewState() {
        return newState;
    }

}
