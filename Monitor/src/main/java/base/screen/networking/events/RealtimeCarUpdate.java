/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.data.RealtimeInfo;

/**
 *
 * @author Leonard
 */
public class RealtimeCarUpdate extends Event {

    private RealtimeInfo info;

    public RealtimeCarUpdate(RealtimeInfo info) {
        this.info = info;
    }

    public RealtimeInfo getInfo() {
        return info;
    }

}
