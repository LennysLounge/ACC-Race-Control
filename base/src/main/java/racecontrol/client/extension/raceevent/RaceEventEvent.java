/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent;

import racecontrol.client.extension.raceevent.entries.RaceEventEntry;
import racecontrol.eventbus.Event;

/**
 * An Event for a thing that happened in the race
 *
 * @author Leonard
 */
public class RaceEventEvent extends Event {

    private RaceEventEntry entry;

    public RaceEventEvent(RaceEventEntry entry) {
        this.entry = entry;
    }

    public RaceEventEntry getEntry() {
        return entry;
    }
}
