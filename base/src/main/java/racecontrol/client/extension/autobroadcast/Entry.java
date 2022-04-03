/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import racecontrol.client.protocol.CarInfo;

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
    /**
     * After a focus change, cars that are not focused get a rating penalty to
     * avoid continuous jumping.
     */
    private float focus;
    /**
     * Rating that combines the proximity and pack ratings.
     */
    private float packProximity;
    /**
     * Pace rating. Faster lap times create higher ratings.
     */
    private float pace;
    /**
     * Pace focus rating. When we are spectating a lap, we want to keep focusing
     * on that lap until it is invalidated or finished.
     */
    private float paceFocus;
    /**
     * A small randomness to avoid ties.
     */
    private float randomness;

    public Entry(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    public float getRating() {
        return 1f * proximity * pack * position * focus;
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

    public float getPackProximity() {
        return packProximity;
    }

    public void setPackProximity(float packProximity) {
        this.packProximity = packProximity;
    }

    public float getFocus() {
        return focus;
    }

    public void setFocus(float focus) {
        this.focus = focus;
    }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public float getPaceFocus() {
        return paceFocus;
    }

    public void setPaceFocus(float paceFocus) {
        this.paceFocus = paceFocus;
    }

    public float getRandomness() {
        return randomness;
    }

    public void setRandomness(float randomness) {
        this.randomness = randomness;
    }
    
    

}
