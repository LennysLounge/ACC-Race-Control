/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.client.data.enums.CarModel;
import static racecontrol.client.data.enums.CarModel.ERROR;

/**
 *
 * @author Leonard
 */
public class CarInfo {

    /**
     * This class's logger.
     */
    private static Logger LOG = Logger.getLogger(CarInfo.class.getName());

    private final int carId;
    private final CarModel carModel;
    private final String teamName;
    private final int carNumber;
    private final byte cupCatergory;
    private final byte currentDriverIndex;
    private final int carNationality;
    private final List<DriverInfo> drivers;
    private final RealtimeInfo realtime;
    /**
     * Car number as a formated string.
     */
    private final String carNumberString;

    public CarInfo() {
        this(0, ERROR, "", 0, (byte) 0, (byte) 0, 0, new LinkedList<>(), new RealtimeInfo());
    }

    public CarInfo(int carId, CarModel carModel, String teamName, int carNumber,
            byte cupCatergory, byte currentDriverIndex, int carNationality,
            List<DriverInfo> drivers, RealtimeInfo realtime) {
        this.carId = carId;
        this.carModel = carModel;
        this.teamName = teamName;
        this.carNumber = carNumber;
        this.cupCatergory = cupCatergory;
        this.currentDriverIndex = currentDriverIndex;
        this.carNationality = carNationality;
        this.drivers = drivers;
        this.realtime = realtime;

        carNumberString = String.format("#%-3d", carNumber);
    }

    public RealtimeInfo getRealtime() {
        return realtime;
    }

    public CarInfo withRealtime(RealtimeInfo realtime) {
        return new CarInfo(carId, carModel, teamName, carNumber, cupCatergory,
                currentDriverIndex, carNationality, drivers, realtime);
    }

    public DriverInfo getDriver() {
        //read driver from the realtime info
        if (realtime != null
                && drivers.size() > realtime.getDriverIndex()) {
            return drivers.get(realtime.getDriverIndex());
        } //if realtime is not available we use the current Driver Index.
        else if (drivers.size() > currentDriverIndex) {
            return drivers.get(currentDriverIndex);
        } //else we return an empty driver.
        else {
            return new DriverInfo();
        }
    }

    public int getCarId() {
        return carId;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getCarNumber() {
        return carNumber;
    }

    public byte getCupCatergory() {
        return cupCatergory;
    }

    public byte getCurrentDriverIndex() {
        return currentDriverIndex;
    }

    public int getCarNationality() {
        return carNationality;
    }

    public List<DriverInfo> getDrivers() {
        return Collections.unmodifiableList(drivers);
    }
    
    public String getCarNumberString(){
        return carNumberString;
    }
}
