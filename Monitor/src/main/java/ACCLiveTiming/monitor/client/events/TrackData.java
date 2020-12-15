/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.client.events;

import acclivetiming.Monitor.eventbus.Event;
import acclivetiming.Monitor.networking.data.TrackInfo;

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
