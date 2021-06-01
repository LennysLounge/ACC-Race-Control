/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.RealtimeInfo;

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
