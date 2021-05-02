/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
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
