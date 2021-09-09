/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.TrackInfo;

/**
 *
 * @author Leonard
 */
public class TrackDataEvent extends Event {

    private TrackInfo info;

    public TrackDataEvent(TrackInfo info) {
        this.info = info;
    }

    public TrackInfo getInfo() {
        return info;
    }

}
