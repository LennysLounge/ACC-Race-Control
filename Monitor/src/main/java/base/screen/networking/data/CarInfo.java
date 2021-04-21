/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class CarInfo {

    private static Logger LOG = Logger.getLogger(CarInfo.class.getName());

    private int carId;
    private byte carModelType;
    private String teamName = "";
    private int carNumber;
    private byte cupCatergory;
    private byte currentDriverIndex;
    private int carNationality;
    private List<DriverInfo> drivers = new LinkedList<>();
    private RealtimeInfo realtime = new RealtimeInfo();

    public CarInfo() {
    }

    public CarInfo(int carId, byte carModelType, String teamName, int carNumber,
            byte cupCatergory, byte currentDriverIndex, int carNationality,
            List<DriverInfo> drivers, RealtimeInfo realtime) {
        this.carId = carId;
        this.carModelType = carModelType;
        this.teamName = teamName;
        this.carNumber = carNumber;
        this.cupCatergory = cupCatergory;
        this.currentDriverIndex = currentDriverIndex;
        this.carNationality = carNationality;
        this.drivers = drivers;
        this.realtime = realtime;
    }

    public RealtimeInfo getRealtime() {
        return realtime;
    }

    public CarInfo withRealtime(RealtimeInfo realtime) {
        return new CarInfo(carId, carModelType, teamName, carNumber, cupCatergory,
                currentDriverIndex, carNationality, drivers, realtime);
    }

    public DriverInfo getDriver() {
        if (drivers.size() > currentDriverIndex) {
            return drivers.get(currentDriverIndex);
        } else {
            return new DriverInfo();
        }
    }

    public int getCarId() {
        return carId;
    }

    public byte getCarModelType() {
        return carModelType;
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
}
