/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.enums.CarLocation;
import ACCLiveTiming.networking.enums.DriverCategory;

/**
 *
 * @author Leonard
 */
public class ListEntry {

    private final CarInfo car;
    
    private final boolean isFocused;

    public ListEntry(CarInfo car, boolean isFocused) {
        this.car = car;
        this.isFocused = isFocused;
    }
    
    public boolean isFocused(){
        return isFocused;
    }
    
    public boolean isConnected(){
        return car.isConnected();
    }

    public String getPosition() {
        return String.valueOf(car.getRealtime().getPosition());
    }

    public String getName() {
        String firstname = car.getDriver().getFirstName();
        String lastname = car.getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s",firstname, lastname);
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
        return String.valueOf(car.getRealtime().getDelta());
    }

    public String getCurrentLap() {
        return "";
    }

    public String getSectorOne() {
        return "";
    }

    public String getSectorTwo() {
        return "";
    }

    public String getSectorThree() {
        return "";
    }

    public String getBestLap() {
        return "";
    }

    public String getLastLap() {
        return "";
    }

    public boolean isInPits() {
        return car.getRealtime().getLocation() != CarLocation.TRACK;
    }

    public DriverCategory getCategory() {
        return car.getDriver().getCategory();
    }

}
