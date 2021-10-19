/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.trackdata;

import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class TrackDataEvent extends Event {

    private final TrackData data;

    public TrackDataEvent(TrackData data) {
        this.data = data;
    }

    public TrackData getTrackData() {
        return data;
    }

}
