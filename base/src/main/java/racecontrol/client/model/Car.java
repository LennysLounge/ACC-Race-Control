/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import java.util.ArrayList;
import java.util.List;
import racecontrol.client.protocol.LapInfo;
import racecontrol.client.protocol.enums.CarLocation;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
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
    public int KMH;
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
     * Current leaderboard position in realtime.
     */
    public int realtimePosition;
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
     * Distance driven in the session. Corrects the error when adding LapCount
     * and spline position.
     */
    public float raceDistance;
    /**
     * Ammount of laps completed.
     */
    public int lapCount;
    /**
     * Delta to current best laptime.
     */
    public int delta;
    /**
     * Best lap.
     */
    public LapInfo bestLap = new LapInfo();
    /**
     * Last completed lap.
     */
    public LapInfo lastLap = new LapInfo();
    /**
     * Current lap.
     */
    public LapInfo currentLap = new LapInfo();
    /**
     * Laptime delta to the session best lap.
     */
    public int deltaToSessionBest;
    /**
     * Car id of the car a position ahead. 0 if no car ahead.
     */
    public int carPositionAhead = 0;
    /**
     * Car id of the car a position behind. 0 if no car behind.
     */
    public int carPositionBehind = 0;
    /**
     * Gap to the car a position ahead. Max int if no car ahead.
     */
    public int gapPositionAhead = Integer.MAX_VALUE;
    /**
     * Gap to the car a position behind. Max int if no car behind.
     */
    public int gapPositionBehind = Integer.MAX_VALUE;
    /**
     * Gap to the leader.
     */
    public int gapToLeader;
    /**
     * laps behind the leader.
     */
    public float lapsBehindLeader;
    /**
     * Car id of the car ahead. 0 if no car ahead.
     */
    public int carAhead = 0;
    /**
     * Car id of the car behind. 0 if no car behind.
     */
    public int carBehind = 0;
    /**
     * Gap to the car ahead. Max int if no car ahead.
     */
    public int gapAhead = Integer.MAX_VALUE;
    /**
     * Gap to the car behind. Max int if no car behind.
     */
    public int gapBehind = Integer.MAX_VALUE;
    /**
     * True if the car is the session best lap time holder.
     */
    public boolean isSessionBestLaptime;
    /**
     * True if the car is currently in focus.
     */
    public boolean isFocused = false;
    /**
     * True if the car is yellow flagged.
     */
    public boolean isYellowFlag = false;
    /**
     * True if the car is white flagged.
     */
    public boolean isWhiteFlag = false;
    /**
     * True if the car is checkered flagged.
     */
    public boolean isCheckeredFlag = false;
    /**
     * Indicates if this car has overtakes someone.
     */
    public int overtakeIndicator;
    /**
     * Maximum speed reached.
     */
    public int maxKMH;
    /**
     * Speed at the speed trap.
     */
    public int speedTrapKMH;
    /**
     * Start position for a race.
     */
    public int raceStartPosition;
    /**
     * True if the race start position is accurate.
     */
    public boolean raceStartPositionAccurate = false;
    /**
     * Time spend in the pitlane for the last pitstop.
     */
    public int pitLaneTime;
    /**
     * Time spend stationary in the pitlane for the last pitstop.
     */
    public int pitLaneTimeStationary;
    /**
     * How many pitstops were done.
     */
    public int pitlaneCount;
    /**
     * True if the pitlane count is accurate.
     */
    public boolean pitlaneCountAccurate;
    /**
     * The stint time of the current driver.
     */
    public int driverStintTime;
    /**
     * True if the driver stint time is accurate.
     */
    public boolean driverStintTimeAccurate;

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
        return bestLap.getLapTimeMS() + delta;
    }

    /**
     * Returns true if the car is currently in the pitlane.
     *
     * @return true if the car is currently in the pitlane.
     */
    public boolean isInPit() {
        return carLocation == PITLANE;
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
        car.lastUpdate = lastUpdate;
        car.connected = connected;
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
        car.KMH = KMH;
        car.yaw = yaw;
        car.pitch = pitch;
        car.roll = roll;
        car.carLocation = carLocation;
        car.position = position;
        car.realtimePosition = realtimePosition;
        car.cupPosition = cupPosition;
        car.trackPosition = trackPosition;
        car.splinePosition = splinePosition;
        car.raceDistance = raceDistance;
        car.lapCount = lapCount;
        car.delta = delta;
        car.bestLap = bestLap;
        car.lastLap = lastLap;
        car.currentLap = currentLap;
        car.deltaToSessionBest = deltaToSessionBest;
        car.carPositionAhead = carPositionAhead;
        car.carPositionBehind = carPositionBehind;
        car.gapPositionAhead = gapPositionAhead;
        car.gapPositionBehind = gapPositionBehind;
        car.gapToLeader = gapToLeader;
        car.lapsBehindLeader = lapsBehindLeader;
        car.carAhead = carAhead;
        car.carBehind = carBehind;
        car.gapAhead = gapAhead;
        car.gapBehind = gapBehind;
        car.isSessionBestLaptime = isSessionBestLaptime;
        car.isFocused = isFocused;
        car.isYellowFlag = isYellowFlag;
        car.isWhiteFlag = isWhiteFlag;
        car.isCheckeredFlag = isCheckeredFlag;
        car.overtakeIndicator = overtakeIndicator;
        car.maxKMH = maxKMH;
        car.speedTrapKMH = speedTrapKMH;
        car.raceStartPosition = raceStartPosition;
        car.raceStartPositionAccurate = raceStartPositionAccurate;
        car.pitLaneTime = pitLaneTime;
        car.pitLaneTimeStationary = pitLaneTimeStationary;
        car.pitlaneCount = pitlaneCount;
        car.pitlaneCountAccurate = pitlaneCountAccurate;
        car.driverStintTime = driverStintTime;
        car.driverStintTimeAccurate = driverStintTimeAccurate;
        return car;
    }

}
