/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.enums.CarLocation;
import ACCLiveTiming.networking.enums.DriverCategory;
import ACCLiveTiming.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class ListEntry {

    private CarInfo car;

    private boolean isFocused;

    private float gap;

    private float gap2;

    private float distanceToFront;

    public ListEntry() {
    }

    public void setCarInfo(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCarInfo() {
        return car;
    }

    public void setDistanceToFront(float distanceToFront) {
        this.distanceToFront = distanceToFront;
    }

    public float getDistanceToFront() {
        return distanceToFront;
    }

    public boolean isFocused() {
        return isFocused;
    }
    
    public void setFocused(boolean isFocused){
        this.isFocused = isFocused;
    }

    public boolean isConnected() {
        return car.isConnected();
    }

    public String getPosition() {
        return String.valueOf(car.getRealtime().getPosition());
    }

    public String getName() {
        String firstname = car.getDriver().getFirstName();
        String lastname = car.getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s", firstname, lastname);
    }

    public String getCarNumber() {
        return String.valueOf(car.getCarNumber());
    }

    public String getLapCount() {
        return String.valueOf(car.getRealtime().getLaps());
    }

    public String getGap() {
        return "";
    }

    public String getToLeader() {
        return "";
    }

    public String getDelta() {
        if(isInPits()){
            return "--.--";
        }
        return TimeUtils.asDelta(car.getRealtime().getDelta());
    }

    public String getCurrentLap() {
        if(isInPits()){
            return "--.--";
        }
        return TimeUtils.asLapTime(car.getRealtime().getCurrentLap().getLapTimeMS());
    }

    public String getSectorOne() {
        return "--.--";
    }

    public String getSectorTwo() {
        return "--.--";
    }

    public String getSectorThree() {
        return "--.--";
    }

    public String getBestLap() {
        return TimeUtils.asLapTime(car.getRealtime().getBestSessionLap().getLapTimeMS());
    }

    public String getLastLap() {
        return TimeUtils.asLapTime(car.getRealtime().getLastLap().getLapTimeMS());
    }

    public boolean isInPits() {
        return car.getRealtime().getLocation() != CarLocation.TRACK;
    }

    public DriverCategory getCategory() {
        return car.getDriver().getCategory();
    }

}
