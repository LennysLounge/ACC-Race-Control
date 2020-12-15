/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
