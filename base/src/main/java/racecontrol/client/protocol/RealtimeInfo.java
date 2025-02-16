/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol;

import racecontrol.client.protocol.enums.CarLocation;

/**
 *
 * @author Leonard
 */
public class RealtimeInfo {

    int carId;
    int driverIndex;
    byte driverCount;
    byte gear;
    float yaw;
    float pitch;
    float roll;
    CarLocation location = CarLocation.NONE;
    int kmh;
    int position;
    int cupPosition;
    int trackPosition;
    float splinePosition;
    int laps;
    int delta;
    LapInfo bestSessionLap = new LapInfo();
    LapInfo lastLap = new LapInfo();
    LapInfo currentLap = new LapInfo();
    private final boolean isDefault;

    public RealtimeInfo() {
        isDefault = true;
    }

    public RealtimeInfo(int carId, int driverIndex, byte driverCount, byte gear, float yaw, float pitch, float roll,
            CarLocation location, int kmh, int position, int cupPosition, int trackPosition, float splinePosition, int laps,
            int delta, LapInfo bestSessionLap, LapInfo lasLap, LapInfo currentLap) {
        this.carId = carId;
        this.driverIndex = driverIndex;
        this.driverCount = driverCount;
        this.gear = gear;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.location = location;
        this.kmh = kmh;
        this.position = position;
        this.cupPosition = cupPosition;
        this.trackPosition = trackPosition;
        this.splinePosition = splinePosition;
        this.laps = laps;
        this.delta = delta;
        this.bestSessionLap = bestSessionLap;
        this.lastLap = lasLap;
        this.currentLap = currentLap;
        this.isDefault = false;
    }

    public int getCarId() {
        return carId;
    }

    public int getDriverIndex() {
        return driverIndex;
    }

    public byte getDriverCount() {
        return driverCount;
    }

    public byte getGear() {
        return gear;
    }

    public float getWorldPositionY() {
        return yaw;
    }

    public float getWorldPositionX() {
        return pitch;
    }

    public float getHeading() {
        return roll;
    }

    public CarLocation getLocation() {
        return location;
    }

    public int getKMH() {
        return kmh;
    }

    public int getPosition() {
        return position;
    }

    public int getCupPosition() {
        return cupPosition;
    }

    public int getTrackPosition() {
        return trackPosition;
    }

    public float getSplinePosition() {
        return splinePosition;
    }

    public int getLaps() {
        return laps;
    }

    public int getDelta() {
        return delta;
    }

    public LapInfo getBestSessionLap() {
        return bestSessionLap;
    }

    public LapInfo getLastLap() {
        return lastLap;
    }

    public LapInfo getCurrentLap() {
        return currentLap;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String toString() {
        return "RealtimeInfo{"
                + "carId=" + carId
                + ", driverIndex=" + driverIndex
                + ", driverCount=" + driverCount
                + ", gear=" + gear
                + ", yaw=" + yaw
                + ", pitch=" + pitch
                + ", roll=" + roll
                + ", location=" + location
                + ", kmh=" + kmh
                + ", position=" + position
                + ", cupPosition=" + cupPosition
                + ", trackPosition=" + trackPosition
                + ", splinePosition=" + splinePosition
                + ", laps=" + laps
                + ", delta=" + delta
                + ", bestSessionLap=" + bestSessionLap
                + ", lastLap=" + lastLap
                + ", currentLap=" + currentLap
                + ", isDefault=" + isDefault
                + '}';
    }

}
