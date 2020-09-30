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
import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.function.Function;

/**
 *
 * @author Leonard
 */
public class LiveTimingEntry extends LPTable.Entry {

    public static final Function<LiveTimingEntry, String> getPosition = (e) -> {
        return String.valueOf(e.getCarInfo().getRealtime().getPosition());
    };

    public static final Function<LiveTimingEntry, String> getName = (e) -> {
        String firstname = e.getCarInfo().getDriver().getFirstName();
        String lastname = e.getCarInfo().getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s", firstname, lastname);
    };

    public static final Function<LiveTimingEntry, String> getCarNumber = (e) -> {
        return String.valueOf(e.getCarInfo().getCarNumber());
    };

    public static final Function<LiveTimingEntry, String> getLapCount = (e) -> {
        return String.valueOf(e.getCarInfo().getRealtime().getLaps());
    };

    public static final Function<LiveTimingEntry, String> getDelta = (e) -> {
        if (e.isInPits()) {
            return "--.--";
        }
        return TimeUtils.asDelta(e.getCarInfo().getRealtime().getDelta());
    };

    public static final Function<LiveTimingEntry, String> getCurrentLap = (e) -> {
        if (e.isInPits()) {
            return "--.--";
        }
        return TimeUtils.asLapTime(e.getCarInfo().getRealtime().getCurrentLap().getLapTimeMS());
    };

    public static final Function<LiveTimingEntry, String> getBestLap = (e) -> {
        return TimeUtils.asLapTime(e.getCarInfo().getRealtime().getBestSessionLap().getLapTimeMS());
    };

    public static final Function<LiveTimingEntry, String> getLastLap = (e) -> {
        return TimeUtils.asLapTime(e.getCarInfo().getRealtime().getLastLap().getLapTimeMS());
    };
    
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

    public String getGap() {
        return "";
    }

    public String getToLeader() {
        return "";
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

    public boolean isInPits() {
        return car.getRealtime().getLocation() != CarLocation.TRACK;
    }

    public DriverCategory getCategory() {
        return car.getDriver().getCategory();
    }

}
