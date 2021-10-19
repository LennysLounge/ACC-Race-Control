/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.eventbus.Event;

/**
 * An event that gets published when a contact occures.
 * @author Leonard
 */
public class ContactEvent extends Event {

    private final ContactInfo info;

    public ContactEvent(ContactInfo info) {
        this.info = info;
    }

    public ContactInfo getInfo() {
        return info;
    }
}
