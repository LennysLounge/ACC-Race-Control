/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.extensions.fullcourseyellow.events;

import acclivetiming.monitor.eventbus.Event;
import acclivetiming.monitor.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class FCYViolation extends Event {

    private final float timeStamp;
    private final CarInfo car;
    private final int speed;

    public FCYViolation(float timeStamp, CarInfo car, int speed) {
        this.timeStamp = timeStamp;
        this.car = car;
        this.speed = speed;
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public CarInfo getCar() {
        return car;
    }

    public int getSpeed() {
        return speed;
    }

}
