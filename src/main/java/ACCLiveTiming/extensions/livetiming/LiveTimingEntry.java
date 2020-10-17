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
import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.function.Function;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LiveTimingEntry extends LPTable.Entry {

    public static final LPTable.Renderer positionRenderer = new LPTable.Renderer() {
        @Override
        public void draw(LPTable.Entry entry) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_RED);
            applet.rect(0, 0, width, height);
            applet.fill(255);
            applet.textAlign(CENTER, CENTER);
            LiveTimingEntry e = (LiveTimingEntry) entry;
            applet.text(e.getPosition(), width / 2f, height / 2f);
        }
    };

    public static final Function<LiveTimingEntry, String> getName = (e) -> {
        String firstname = e.getCarInfo().getDriver().getFirstName();
        String lastname = e.getCarInfo().getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        return String.format("%s. %s", firstname, lastname);
    };

    public static final LPTable.Renderer pitRenderer = new LPTable.Renderer() {
        @Override
        public void draw(LPTable.Entry entry) {
            LiveTimingEntry e = (LiveTimingEntry) entry;
            if (e.isInPits()) {
                applet.noStroke();
                applet.fill(LookAndFeel.COLOR_WHITE);
                applet.rect(0, 0, width, height);
                applet.fill(0);
                applet.textAlign(CENTER, CENTER);
                applet.textSize(12);
                applet.text("P", width / 2f, height / 2f);
                applet.textSize(LookAndFeel.TEXT_SIZE);

            } else {
                super.draw(entry);
            }
        }
    };

    public static final LPTable.Renderer carNumberRenderer = new LPTable.Renderer() {
        @Override
        public void draw(LPTable.Entry entry) {
            LiveTimingEntry e = (LiveTimingEntry) entry;
            int backColor = 0;
            int frontColor = 0;
            switch (e.getCategory()) {
                case BRONZE:
                    backColor = LookAndFeel.COLOR_RED;
                    frontColor = LookAndFeel.COLOR_BLACK;
                    break;
                case SILVER:
                    backColor = LookAndFeel.COLOR_GRAY;
                    frontColor = LookAndFeel.COLOR_WHITE;
                    break;
                case GOLD:
                case PLATINUM:
                    backColor = LookAndFeel.COLOR_WHITE;
                    frontColor = LookAndFeel.COLOR_BLACK;
                    break;
            }
            applet.noStroke();
            applet.fill(backColor);
            applet.rect(0, 0, width, height);
            applet.fill(frontColor);
            applet.textAlign(CENTER, CENTER);
            applet.text(e.getCarNumber(), width / 2f, height / 2f);
        }
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

    public String getPosition() {
        return String.valueOf(car.getRealtime().getPosition());
    }

    public String getCarNumber() {
        return String.valueOf(car.getCarNumber());
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
