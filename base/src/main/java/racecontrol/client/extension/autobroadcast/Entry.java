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
    private float proximityFont;
    private float proximityRear;
    private float proximity;
    /**
     * Pack rating. The more cars are close together the higher this rating.
     */
    private float pack;
    private float packBack;
    private float packFront;
    /**
     * Position rating. Higher of the field gives a higher position rating to
     * prioitise higher position drivers.
     */
    private float position;
    /**
     * After a focus change, cars that are not focused get a rating penalty to
     * avoid continuous jumping.
     */
    private float focusFast;
    /**
     * After a focus change, cars that are not focused get a rating penalty to
     * avoid continuous jumping.
     */
    private float focusSlow;

    public Entry(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    public float getRating() {
        return 1f * proximity * position * focusFast * focusSlow * pack;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public float getProximityFront() {
        return proximityFont;
    }

    public void setProximityFont(float proximityFont) {
        this.proximityFont = proximityFont;
    }

    public float getProximityRear() {
        return proximityRear;
    }

    public void setProximityRear(float proximityRear) {
        this.proximityRear = proximityRear;
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public float getFocusFast() {
        return focusFast;
    }

    public void setFocusFast(float focusFast) {
        this.focusFast = focusFast;
    }

    public float getFocusSlow() {
        return focusSlow;
    }

    public void setFocusSlow(float focusSlow) {
        this.focusSlow = focusSlow;
    }

    public float getPack() {
        return pack;
    }

    public void setPack(float pack) {
        this.pack = pack;
    }

    public float getPackBack() {
        return packBack;
    }

    public void setPackBack(float packBack) {
        this.packBack = packBack;
    }

    public float getPackFront() {
        return packFront;
    }

    public void setPackFront(float packFront) {
        this.packFront = packFront;
    }
    
    

}
