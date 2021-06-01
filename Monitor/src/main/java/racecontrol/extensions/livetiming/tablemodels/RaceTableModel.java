/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.livetiming.tablemodels;

import racecontrol.extensions.livetiming.LiveTimingEntry;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.data.enums.LapType;
import racecontrol.utility.TimeUtils;
import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_ORANGE;
import static racecontrol.visualisation.LookAndFeel.COLOR_WHITE;
import racecontrol.visualisation.gui.LPTable;
import racecontrol.visualisation.gui.LPTableColumn;
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
            .setTextAlign(CENTER)
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
