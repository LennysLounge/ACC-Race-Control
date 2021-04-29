/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming.tablemodels;

import base.screen.extensions.livetiming.LiveTimingEntry;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.LapInfo;
import base.screen.networking.enums.CarLocation;
import base.screen.networking.enums.LapType;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_ORANGE;
import static base.screen.visualisation.LookAndFeel.COLOR_WHITE;
import base.screen.visualisation.gui.LPTable;
import base.screen.visualisation.gui.LPTableColumn;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class RaceTableModel
        extends QualifyingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Gap")
            .setCellRenderer(gapRenderer)
            .setMaxWidth(150),
            new LPTableColumn("To Leader")
            .setCellRenderer(gapToLeaderRenderer)
            .setMaxWidth(150),
            new LPTableColumn("Lap")
            .setCellRenderer(lapTimeRenderer),
            new LPTableColumn("Last")
            .setTextAlign(CENTER),
            new LPTableColumn("Best")
            .setTextAlign(CENTER),
            new LPTableColumn("Laps")
            .setTextAlign(CENTER),
            new LPTableColumn("")
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        CarInfo car = getEntry(row).getCarInfo();

        switch (column) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return getEntry(row);
            case 7:
                return TimeUtils.asLapTime(car.getRealtime().getLastLap().getLapTimeMS());
            case 8:
                return TimeUtils.asLapTime(car.getRealtime().getBestSessionLap().getLapTimeMS());
            case 9:
                return String.valueOf(car.getRealtime().getLaps());
            case 10:
                return "";
        }
        return "-";
    }

    private final LPTable.CellRenderer gapRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        LiveTimingEntry entry = ((LiveTimingEntry) object);
        String gap = TimeUtils.asGap(entry.getGap());
        if (entry.getCarInfo().getRealtime().getPosition() == 1
                && entry.getGap() == 0) {
            gap = "--";
        }
        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        if (entry.getGap() < 1000 && entry.getGap() > 0) {
            applet.fill(COLOR_ORANGE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.text(gap, width - 20, height / 2);
    };

    private final LPTable.CellRenderer gapToLeaderRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        LiveTimingEntry entry = ((LiveTimingEntry) object);

        String text = "--";
        if (entry.showLapsBehind()) {
            text = String.format("+%d Laps", entry.getLapsBehind());
        } else {
            if (entry.getGapToLeader() == 0) {
                text = "Leader";
            } else {
                text = TimeUtils.asGap(entry.getGapToLeader());
            }
        }

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(text, width - 20, height / 2);
    };

    private final LPTable.CellRenderer lapTimeRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        LapInfo currentLap = car.getRealtime().getCurrentLap();
        String text = "--";
        applet.fill(COLOR_WHITE);
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (currentLap.isInvalid()) {
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

}
