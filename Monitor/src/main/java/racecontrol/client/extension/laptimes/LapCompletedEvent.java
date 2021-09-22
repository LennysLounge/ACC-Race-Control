/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.laptimes;

import racecontrol.client.data.CarInfo;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class LapCompletedEvent 
    extends Event{
    
    private final CarInfo car;
    
    private final int lapTime;

    public LapCompletedEvent(CarInfo car, int lapTime) {
        this.car = car;
        this.lapTime = lapTime;
    }

    public CarInfo getCar() {
        return car;
    }

    public int getLapTime() {
        return lapTime;
    }
    
    
    
}
