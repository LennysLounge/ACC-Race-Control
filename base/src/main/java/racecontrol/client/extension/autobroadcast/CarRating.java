/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import racecontrol.client.model.Car;

/**
 *
 * Represents a car on track with their broadcast propability ratings.
 *
 * @author Leonard
 */
public class CarRating {

    /**
     * Car info for this entry.
     */
    public final Car car;
    /**
     * Screen time for this car.
     */
    public int screenTime;
    /**
     * Screen time for this car.
     */
    public float screenTimeError;
    /**
     * The more cars are close to this car the higher this rating.
     */
    public float proximity;
    /**
     * The higher the position of this car is the higher the rating.
     */
    public float position;
    /**
     * Focus delay to stop rapid switching.
     */
    public float focus;
    /**
     * A very small value used to brake ties.
     */
    public float tieBraker;

    public CarRating(Car car) {
        this.car = car;
    }

    public float getRating() {
        return getRatingNoFocus() * focus;
    }

    public float getRatingNoFocus() {
        return proximity * position + screenTimeError + tieBraker;
    }
}
