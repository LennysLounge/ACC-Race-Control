/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.BroadcastingEvent;

/**
 *
 * @author Leonard
 */
public class BroadcastingEventEvent extends Event {

    private BroadcastingEvent event;

    public BroadcastingEventEvent(BroadcastingEvent event) {
        this.event = event;
    }

    public BroadcastingEvent getEvent() {
        return event;
    }

}
