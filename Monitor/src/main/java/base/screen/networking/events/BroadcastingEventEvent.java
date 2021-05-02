/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.eventbus.Event;
import base.screen.networking.data.BroadcastingEvent;

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
