/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.extensions.livetiming;

import acclivetiming.monitor.networking.data.CarInfo;
import acclivetiming.monitor.networking.data.LapInfo;
import acclivetiming.monitor.networking.enums.CarLocation;
import acclivetiming.monitor.networking.enums.DriverCategory;
import static acclivetiming.monitor.networking.enums.DriverCategory.BRONZE;
import static acclivetiming.monitor.networking.enums.DriverCategory.GOLD;
import static acclivetiming.monitor.networking.enums.DriverCategory.PLATINUM;
import static acclivetiming.monitor.networking.enums.DriverCategory.SILVER;
import acclivetiming.monitor.networking.enums.LapType;
import acclivetiming.monitor.utility.TimeUtils;
import acclivetiming.monitor.visualisation.LookAndFeel;
import acclivetiming.monitor.visualisation.gui.LPTableColumn;
import acclivetiming.monitor.visualisation.gui.LPTable;
import acclivetiming.monitor.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LiveTimingTableModel extends TableModel {

    /**
     * Ordered list of car entries
     */
    private List<CarInfo> entries = new LinkedList<>();
    /**
     * Car id of the focused car.
     */
    private int focusedCarId = -1;

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("P")
            .setMinWidth(40)
            .setMaxWidth(40)
            .setCellRenderer(positionRenderer),
            new LPTableColumn("Name")
            .setMaxWidth(240),
            new LPTableColumn("")
            .setMaxWidth(16)
            .setMinWidth(16)
            .setCellRenderer(pitRenderer),
            new LPTableColumn("#")
            .setMinWidth(60)
            .setMaxWidth(60)
            .setCellRenderer(carNumberRenderer),
            new LPTableColumn("Lap")
            .setCellRenderer(lapTimeRenderer),
            new LPTableColumn("Delta")
            .setCellRenderer(deltaRenderer)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        CarInfo car = entries.get(row);
        switch (column) {
            case 0:
                return new Tuple(
                        car.getRealtime().getPosition(),
                        car.getCarId() == focusedCarId
                );
            case 1:
                String firstname = car.getDriver().getFirstName();
                String lastname = car.getDriver().getLastName();
                firstname = firstname.substring(0, Math.min(firstname.length(), 1));
                return String.format("%s. %s", firstname, lastname);
            case 2:
                return car.getRealtime().getLocation() != CarLocation.TRACK;
            case 3:
                return new Tuple(
                        car.getCarNumber(),
                        car.getDriver().getCategory()
                );
            case 4:
                return car;
            case 5:
                return car;
        }
        return "-";
    }

    public void setEntries(List<CarInfo> entries) {
        this.entries = entries;
    }

    public void setFocusedCarId(int carId) {
        focusedCarId = carId;
    }

    private class Tuple {

        public Object left;
        public Object right;

        public Tuple(Object left, Object right) {
            this.left = left;
            this.right = right;
        }
    }

    private final LPTable.CellRenderer positionRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Tuple t = (Tuple) object;
        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if ((boolean) t.right) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, width - 2, height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.text(String.valueOf(t.left),
                width / 2f, height / 2f);
    };

    private final LPTable.CellRenderer pitRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        boolean isInPits = (boolean) object;
        if (isInPits) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_WHITE);
            applet.rect(1, 1, width - 2, height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(12);
            applet.text("P", width / 2f, height / 2f);
            applet.textSize(LookAndFeel.TEXT_SIZE);
        }
    };

    private final LPTable.CellRenderer carNumberRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Tuple t = (Tuple) object;
        int backColor = 0;
        int frontColor = 0;
        switch ((DriverCategory) t.right) {
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
        applet.rect(1, 1, width - 2, height - 2);
        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.text(String.valueOf(t.left), width / 2f, height / 2f);
    };

    private final LPTable.CellRenderer lapTimeRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = (CarInfo) object;
        LapInfo currentLap = car.getRealtime().getCurrentLap();
        String text = "";
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (currentLap.getIsInvalid()) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(currentLap.getLapTimeMS());
        } else if (car.getRealtime().getLocation() == CarLocation.TRACK) {
            applet.fill(0, 0, 0, 75);
            text = currentLap.getType().name();
        }
        applet.textAlign(CENTER, CENTER);
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer deltaRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = (CarInfo) object;
        LapInfo currentLap = car.getRealtime().getCurrentLap();

        String text = "";
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {

            applet.fill(LookAndFeel.COLOR_RACE);
            if (car.getRealtime().getDelta() > 0) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asDelta(car.getRealtime().getDelta());
            applet.textAlign(CENTER, CENTER);
            applet.text(text, width / 2, height / 2);
        }
    };

}
