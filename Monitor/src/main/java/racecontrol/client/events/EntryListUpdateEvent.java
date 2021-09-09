/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class EntryListUpdateEvent extends Event {

    private List<Integer> carIds;

    public EntryListUpdateEvent(List<Integer> carIds) {
        this.carIds = carIds;
    }

    public List<Integer> getCarIds() {
        return carIds;
    }

}
