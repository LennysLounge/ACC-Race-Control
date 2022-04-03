/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import java.util.ArrayList;
import java.util.List;
import racecontrol.client.protocol.CarInfo;
import racecontrol.client.protocol.DriverInfo;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.enums.CarModel;
import racecontrol.client.protocol.enums.Nationality;

/**
 * Represents a car.
 *
 * @author Leonard
 */
public class Car {

    /**
     * Raw realtime info.
     */
    public RealtimeInfo realtimeRaw = new RealtimeInfo();
    /**
     * Car id.
     */
    public int id;
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
    public List<DriverInfo> drivers = new ArrayList<>();

    /**
     * Returns the car number formated as "#XXX".
     *
     * @return The car number formated as "#XXX".
     */
    public String carNumberString() {
        return String.format("#%-3d", carNumber);
    }

    /**
     * Returns the current driver.
     *
     * @return The current driver.
     */
    public DriverInfo getDriver() {
        //read driver from the realtime info
        if (realtimeRaw != null
                && drivers.size() > realtimeRaw.getDriverIndex()) {
            return drivers.get(realtimeRaw.getDriverIndex());
        } else if (drivers.size() > driverIndex) {
            //if realtime is not available we use the current Driver Index.
            return drivers.get(driverIndex);
        } else {
            //else we return an empty driver.
            return new DriverInfo();
        }
    }

    public Car copy() {
        Car car = new Car();
        car.realtimeRaw = realtimeRaw;
        car.id = id;
        car.carModel = carModel;
        car.teamName = teamName;
        car.carNumber = carNumber;
        car.cupCategory = cupCategory;
        car.driverIndex = driverIndex;
        car.nationality = nationality;
        car.drivers = new ArrayList<>(drivers);
        return car;
    }
}
