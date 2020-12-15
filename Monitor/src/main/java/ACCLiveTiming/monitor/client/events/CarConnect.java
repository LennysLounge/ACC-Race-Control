/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.client.events;

import acclivetiming.Monitor.eventbus.Event;
import acclivetiming.Monitor.networking.data.CarInfo;

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
