/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class EntryListCarUpdateEvent extends Event {

    private CarInfo info;

    public EntryListCarUpdateEvent(CarInfo info) {
        this.info = info;
    }

    public CarInfo getSessionInfo() {
        return info;
    }

}
