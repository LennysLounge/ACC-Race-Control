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
public class CarConnectedEvent extends Event {

    /**
     * The car that has connected.
     */
    private Car car;

    public CarConnectedEvent(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

}
