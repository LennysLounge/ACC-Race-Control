/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.client.events;

import acclivetiming.monitor.eventbus.Event;
import acclivetiming.monitor.networking.data.TrackInfo;

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
