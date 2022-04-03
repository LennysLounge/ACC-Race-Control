/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.protocol.CarInfo;

/**
 *
 * @author Leonard
 */
public class CarDisconnectedEvent extends Event {

    private CarInfo car;

    public CarDisconnectedEvent(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCar() {
        return car;
    }

}
