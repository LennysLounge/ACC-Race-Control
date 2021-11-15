/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.dangerdetection;

import racecontrol.client.data.CarInfo;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class YellowFlagEvent
        extends Event {
    
    private final CarInfo car;
    
    public YellowFlagEvent(CarInfo car){
        this.car = car;
    }
    
    public CarInfo getCar(){
        return car;
    }

}
