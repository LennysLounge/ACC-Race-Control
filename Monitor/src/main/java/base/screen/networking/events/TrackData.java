/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.data.TrackInfo;

/**
 *
 * @author Leonard
 */
public class TrackData extends Event {

    private TrackInfo info;

    public TrackData(TrackInfo info) {
        this.info = info;
    }

    public TrackInfo getInfo() {
        return info;
    }

}
