/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.eventbus.Event;

/**
 * An event for when the enabled state of the contact extension has changed.
 *
 * @author Leonard
 */
public class ContactExtensionEnabledEvent
        extends Event {

    /**
     * The new state of the contact extension.
     */
    private final boolean newState;

    public ContactExtensionEnabledEvent(boolean state) {
        this.newState = state;
    }

    public boolean getNewState() {
        return newState;
    }

}
