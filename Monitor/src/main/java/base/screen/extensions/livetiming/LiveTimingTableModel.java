/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.networking.data.CarInfo;
import base.screen.networking.data.LapInfo;
import base.screen.networking.enums.CarLocation;
import base.screen.networking.enums.DriverCategory;
import static base.screen.networking.enums.DriverCategory.BRONZE;
import static base.screen.networking.enums.DriverCategory.GOLD;
import static base.screen.networking.enums.DriverCategory.PLATINUM;
import static base.screen.networking.enums.DriverCategory.SILVER;
import base.screen.networking.enums.LapType;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_RED;
import static base.screen.visualisation.LookAndFeel.COLOR_PURPLE;
import static base.screen.visualisation.LookAndFeel.COLOR_WHITE;
import base.screen.visualisation.gui.LPTableColumn;
import base.screen.visualisation.gui.LPTable;
import base.screen.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

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
    /**
     * The best lap of the session.
     */
    private LapInfo sessionBestLap;

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("P")
            .setMinWidth(50)
            .setMaxWidth(50)
            .setCellRenderer(positionRenderer),
            new LPTableColumn("Name")
            .setMaxWidth(240)
            .setGrowthRate(3)
            .setCellRenderer(nameRenderer),
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
            .setCellRenderer(deltaRenderer),
            new LPTableColumn("Best")
            .setCellRenderer(bestLapRenderer),
            new LPTableColumn("Gap")
            .setCellRenderer(gapRenderer),
            new LPTableColumn("S1")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S2")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S3")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("Laps")
            .setCellRenderer(lapsRenderer)
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
            case 5:
            case 6:
            case 7:
                return car;
            case 8:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        0
                );
            case 9:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        1
                );
            case 10:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        2
                );
            case 11:
                return String.valueOf(car.getRealtime().getLaps());
        }
        return "-";
    }

    public void setEntries(List<CarInfo> entries) {
        this.entries = entries;
    }

    public CarInfo getEntry(int row) {
        if (row < entries.size()) {
            return entries.get(row);
        }
        return null;
    }

    public void setFocusedCarId(int carId) {
        focusedCarId = carId;
    }

    public LapInfo getSessionBestLap() {
        return sessionBestLap;
    }

    public void setSessionBestLap(LapInfo sessionBestLap) {
        this.sessionBestLap = sessionBestLap;
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
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(t.left),
                width / 2f, height / 2f);
    };

    private final LPTable.CellRenderer nameRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {

        String name = (String) object;
        if (isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(1, 1, width - 1, height - 2);
        }

        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(name, height / 4f, height / 2f);
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
        if (isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, width - 1, height - 2);
        }
        if (isInPits) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_WHITE);
            applet.rect(1, 1, width - 2, height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(12);
            applet.text("P", width / 2f, height / 2f);
            applet.textFont(LookAndFeel.fontMedium());
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
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(t.left), width / 2f, height / 2f);
    };

    private final LPTable.CellRenderer bestLapRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = (CarInfo) object;
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.noStroke();
        if (bestLapTime == sessionBestLap.getLapTimeMS()) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer gapRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = (CarInfo) object;
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            int sessionBestLapTime = sessionBestLap.getLapTimeMS();
            int diff = bestLapTime - sessionBestLapTime;
            if (diff != 0) {
                text = TimeUtils.asDelta(diff);
            }

        }
        applet.noStroke();
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
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
        String text = "--";
        applet.fill(COLOR_WHITE);
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (currentLap.getIsInvalid()) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(currentLap.getLapTimeMS());
        } else if (car.getRealtime().getLocation() == CarLocation.TRACK) {
            text = currentLap.getType().name();
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
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

        String text = "--";
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {

            applet.fill(LookAndFeel.COLOR_RACE);
            if (car.getRealtime().getDelta() > 0) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asDelta(car.getRealtime().getDelta());
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer sectorRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Tuple input = (Tuple) object;
        int sectorIndex = (int) input.right;
        List<Integer> splits = ((LapInfo) input.left).getSplits();

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (sectorIndex < splits.size()) {
            if (splits.get(sectorIndex) != Integer.MAX_VALUE) {
                text = TimeUtils.asLapTime(splits.get(sectorIndex));
                if (Objects.equals(splits.get(sectorIndex), sessionBestLap.getSplits().get(sectorIndex))) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer lapsRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text((String) object, width / 2, height / 2);
    };

}
