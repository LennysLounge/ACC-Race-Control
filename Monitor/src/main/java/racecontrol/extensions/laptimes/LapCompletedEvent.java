/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.laptimes;

import racecontrol.client.data.CarInfo;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class LapCompletedEvent 
    extends Event{
    
    private final CarInfo car;
    
    private final float lapTime;

    public LapCompletedEvent(CarInfo car, float lapTime) {
        this.car = car;
        this.lapTime = lapTime;
    }

    public CarInfo getCar() {
        return car;
    }

    public float getLapTime() {
        return lapTime;
    }
    
    
    
}
