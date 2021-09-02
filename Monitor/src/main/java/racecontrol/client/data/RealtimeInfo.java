/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

import racecontrol.client.data.enums.CarLocation;

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

    public RealtimeInfo() {
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
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

}
