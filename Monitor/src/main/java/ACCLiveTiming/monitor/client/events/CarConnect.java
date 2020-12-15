/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.client.events;

import acclivetiming.monitor.eventbus.Event;
import acclivetiming.monitor.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class CarConnect extends Event {

    private CarInfo car;

    public CarConnect(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCar() {
        return car;
    }

}
