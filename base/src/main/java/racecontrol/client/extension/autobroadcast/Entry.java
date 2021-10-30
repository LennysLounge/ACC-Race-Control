/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import racecontrol.client.data.CarInfo;

/**
 *
 * Represents a car on track with their broadcast propability ratings.
 *
 * @author Leonard
 */
public class Entry {

    /**
     * Car info for this entry.
     */
    private final CarInfo carInfo;
    /**
     * Proximit rating for this entry. The closer this car is following a car
     * the higher this value is.
     */
    private final float proximity;
    private final float overtake;
    private final float change;
    private final float proximityDelta;

    public Entry(CarInfo carInfo) {
        this(carInfo, 0f, 0f, 0f, 0f);
    }

    private Entry(CarInfo carInfo,
            float proximity,
            float overtake,
            float change,
            float proximityDelta) {
        this.carInfo = carInfo;
        this.proximity = proximity;
        this.overtake = overtake;
        this.change = change;
        this.proximityDelta = proximityDelta;
    }

    public float getRating() {
        return (proximity
                + overtake
                + change) / 2;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public Entry withProximity(float proximity) {
        return new Entry(carInfo, proximity, overtake, change, proximityDelta);
    }

    public float getProximity() {
        return proximity;
    }

    public Entry withOvertake(float overtake) {
        return new Entry(carInfo, proximity, overtake, change, proximityDelta);
    }

    public float getOvertake() {
        return overtake;
    }

    public Entry withChange(float change) {
        return new Entry(carInfo, proximity, overtake, change, proximityDelta);
    }

    public float getChange() {
        return change;
    }
    
    public Entry withProximityDelta(float proximityDelta) {
        return new Entry(carInfo, proximity, overtake, change, proximityDelta);
    }

    public float getProximityDelta() {
        return proximityDelta;
    }

}
