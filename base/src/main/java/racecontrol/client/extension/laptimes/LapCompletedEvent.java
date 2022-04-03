/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.laptimes;

import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class LapCompletedEvent 
    extends Event{
    
    private final Car car;
    
    private final int lapTime;

    public LapCompletedEvent(Car car, int lapTime) {
        this.car = car;
        this.lapTime = lapTime;
    }

    public Car getCar() {
        return car;
    }

    public int getLapTime() {
        return lapTime;
    }
    
    
    
}
