/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import racecontrol.client.data.CarInfo;
import racecontrol.client.data.RealtimeInfo;

/**
 * Represents a car.
 *
 * @author Leonard
 */
public class Car {

    /**
     * Raw car info.
     */
    public CarInfo raw = new CarInfo();
    /**
     * Raw realtime info.
     */
    public RealtimeInfo realtimeRaw = new RealtimeInfo();

    public Car copy() {
        Car car = new Car();
        car.raw = raw;
        car.realtimeRaw = realtimeRaw;
        return car;
    }
}
