/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.dangerdetection;

import racecontrol.client.data.CarInfo;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class YellowFlagEvent
        extends Event {

    /**
     * The car that caused the yellow flag.
     */
    private final CarInfo car;
    /**
     * Session timestamp.
     */
    private final int sessionTime;
    /**
     * id
     */
    private final int id;

    public YellowFlagEvent(CarInfo car,
            int sessionTime,
            int id) {
        this.car = car;
        this.sessionTime = sessionTime;
        this.id = id;
    }

    public CarInfo getCar() {
        return car;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public int getId() {
        return id;
    }

}
