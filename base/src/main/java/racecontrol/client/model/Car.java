/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import java.util.ArrayList;
import java.util.List;
import racecontrol.client.protocol.LapInfo;
import racecontrol.client.protocol.enums.CarLocation;
import racecontrol.client.protocol.enums.CarModel;
import racecontrol.client.protocol.enums.Nationality;

/**
 * Represents a car.
 *
 * @author Leonard
 */
public class Car {

    /**
     * Car id.
     */
    public int id;
    /**
     * Timestamp of the last update to this car.
     */
    public long lastUpdate = 0;
    /**
     * True if the car is currently connected.
     */
    public boolean connected = false;
    /**
     * The car model.
     */
    public CarModel carModel = CarModel.ERROR;
    /**
     * Team name.
     */
    public String teamName = "";
    /**
     * The car number.
     */
    public int carNumber;
    /**
     * Cup category.
     */
    public int cupCategory;
    /**
     * Currently driving driver index.
     */
    public int driverIndex;
    /**
     * The cars nationality.
     */
    public Nationality nationality = Nationality.ANY;
    /**
     * List of drivers on this car.
     */
    public List<Driver> drivers = new ArrayList<>();
    /**
     * Currently driving driver index.
     */
    public int driverIndexRealtime;
    /**
     * Ammount of drivers for this car.
     */
    public int driverCount;
    /**
     * Current gear the car is in.
     */
    public int gear;
    /**
     * Speed of the car.
     */
    public int kmh;
    /**
     * Yaw of the car.
     */
    public float yaw;
    /**
     * Car pitch.
     */
    public float pitch;
    /**
     * Car roll.
     */
    public float roll;
    /**
     * Location of the car.
     */
    public CarLocation carLocation = CarLocation.NONE;
    /**
     * Current leaderboard position.
     */
    public int position;
    /**
     * Current leaderboard position in the cup.
     */
    public int cupPosition;
    /**
     * Position on track? Not verified.
     */
    public int trackPosition;
    /**
     * Position along the track spline.
     */
    public float splinePosition;
    /**
     * Ammount of laps completed.
     */
    public int lapCount;
    /**
     * Delta to current best laptime.
     */
    public int delta;
    /**
     * Session best lap.
     */
    public LapInfo sessionBestLap = new LapInfo();
    /**
     * Last completed lap.
     */
    public LapInfo lastLap = new LapInfo();
    /**
     * Current lap.
     */
    public LapInfo currentLap = new LapInfo();

    /**
     * Returns the car number formated as "#XXX".
     *
     * @return The car number formated as "#XXX".
     */
    public String carNumberString() {
        return String.format("#%-3d", carNumber);
    }

    /**
     * Returns the predicted lap time.
     *
     * @return the predicted lap time.
     */
    public int predictedLapTime() {
        return sessionBestLap.getLapTimeMS() + delta;
    }

    /**
     * Returns the current driver.
     *
     * @return The current driver.
     */
    public Driver getDriver() {
        //read driver from the realtime info
        if (drivers.size() > driverIndexRealtime) {
            return drivers.get(driverIndexRealtime);
        } else if (drivers.size() > driverIndex) {
            //if realtime is not available we use the current Driver Index.
            return drivers.get(driverIndex);
        } else {
            //else we return an empty driver.
            return new Driver();
        }
    }

    public synchronized Car copy() {
        Car car = new Car();
        car.id = id;
        car.carModel = carModel;
        car.teamName = teamName;
        car.carNumber = carNumber;
        car.cupCategory = cupCategory;
        car.driverIndex = driverIndex;
        car.nationality = nationality;
        car.drivers = new ArrayList<>(drivers);
        car.driverIndexRealtime = driverIndexRealtime;
        car.driverCount = driverCount;
        car.gear = gear;
        car.yaw = yaw;
        car.pitch = pitch;
        car.roll = roll;
        car.carLocation = carLocation;
        car.kmh = kmh;
        car.position = position;
        car.cupPosition = cupPosition;
        car.trackPosition = trackPosition;
        car.splinePosition = splinePosition;
        car.lapCount = lapCount;
        car.delta = delta;
        car.sessionBestLap = sessionBestLap;
        car.lastLap = lastLap;
        car.currentLap = currentLap;
        return car;
    }

}
