/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class CarDisconnect extends Event {

    private CarInfo car;

    public CarDisconnect(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCar() {
        return car;
    }

}
