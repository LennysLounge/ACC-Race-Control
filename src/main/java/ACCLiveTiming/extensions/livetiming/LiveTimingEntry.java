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
public class LiveTimingEntry {

    /**
     * Car this entry represents.
     */
    private CarInfo car;
    /**
     * Indicates that this car is currently focused on in game.
     */
    private boolean isFocused;

    public LiveTimingEntry() {
    }

    public void setCarInfo(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCarInfo() {
        return car;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean isFocused) {
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

    public boolean isInPits() {
        return car.getRealtime().getLocation() != CarLocation.TRACK;
    }

    public DriverCategory getCategory() {
        return car.getDriver().getCategory();
    }

}
