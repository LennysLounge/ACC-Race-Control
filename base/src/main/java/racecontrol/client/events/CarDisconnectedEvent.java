/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class CarDisconnectedEvent extends Event {

    private Car car;

    public CarDisconnectedEvent(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

}
