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
import racecontrol.visualisation.gui.LPTable.RenderContext;

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
            .setMinWidth(100)
            .setMaxWidth(150),
            new LPTableColumn("To Leader")
            .setMinWidth(100)
            .setPriority(-1)
            .setCellRenderer(gapToLeaderRenderer)
            .setMaxWidth(150),
            new LPTableColumn("Lap")
            .setMinWidth(100)
            .setPriority(-1)
            .setCellRenderer(lapTimeRenderer),
            new LPTableColumn("Last")
            .setMinWidth(100)
            .setTextAlign(CENTER),
            new LPTableColumn("Best")
            .setMinWidth(100)
            .setTextAlign(CENTER),
            new LPTableColumn("Laps")
            .setMinWidth(70)
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
            RenderContext context) -> {
        LiveTimingEntry entry = ((LiveTimingEntry) context.object);
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
        applet.text(gap, context.width - 20, context.height / 2);
    };

    private final LPTable.CellRenderer gapToLeaderRenderer = (
            PApplet applet,
            RenderContext context) -> {
        LiveTimingEntry entry = ((LiveTimingEntry) context.object);

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
        applet.text(text, context.width - 20, context.height / 2);
    };

    private final LPTable.CellRenderer lapTimeRenderer = (
            PApplet applet,
            RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
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
        applet.text(text, context.width / 2, context.height / 2);
    };

}
