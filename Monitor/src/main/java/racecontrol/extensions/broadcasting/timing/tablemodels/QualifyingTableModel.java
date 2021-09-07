/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting.timing.tablemodels;

import racecontrol.extensions.broadcasting.timing.LiveTimingEntry;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.data.enums.LapType;
import racecontrol.utility.TimeUtils;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_PURPLE;
import static racecontrol.LookAndFeel.COLOR_WHITE;
import racecontrol.lpgui.gui.LPTableColumn;
import racecontrol.lpgui.gui.LPTable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.lpgui.gui.LPTable.RenderContext;

/**
 *
 * @author Leonard
 */
public class QualifyingTableModel
        extends LiveTimingTableModel {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(QualifyingTableModel.class.getName());

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Lap")
            .setMinWidth(100)
            .setCellRenderer(lapTimeRenderer),
            new LPTableColumn("Delta")
            .setMinWidth(100)
            .setCellRenderer(deltaRenderer),
            new LPTableColumn("Best")
            .setMinWidth(100)
            .setCellRenderer(bestLapRenderer),
            new LPTableColumn("Gap")
            .setMinWidth(100)
            .setCellRenderer(gapRenderer),
            new LPTableColumn("S1")
            .setMinWidth(100)
            .setPriority(-1)
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S2")
            .setMinWidth(100)
            .setPriority(-1)
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S3")
            .setMinWidth(100)
            .setPriority(-1)
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("Laps")
            .setMinWidth(100)
            .setCellRenderer(lapsRenderer)
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
            case 7:
            case 11:
                return getEntry(row);
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
        }
        return "-";
    }

    private class Tuple {

        public Object left;
        public Object right;

        public Tuple(Object left, Object right) {
            this.left = left;
            this.right = right;
        }
    }

    private final LPTable.CellRenderer bestLapRenderer = (
            PApplet applet,
            RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.noStroke();
        if (bestLapTime == getSessionBestLap().getLapTimeMS()) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    };

    private final LPTable.CellRenderer gapRenderer = (
            PApplet applet,
            RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            int sessionBestLapTime = getSessionBestLap().getLapTimeMS();
            int diff = bestLapTime - sessionBestLapTime;
            if (diff != 0) {
                text = TimeUtils.asDelta(diff);
            }

        }
        applet.noStroke();
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
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

    private final LPTable.CellRenderer deltaRenderer = (
            PApplet applet,
            RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
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
        applet.text(text, context.width / 2, context.height / 2);
    };

    private final LPTable.CellRenderer sectorRenderer = (
            PApplet applet,
            RenderContext context) -> {
        Tuple input = (Tuple) context.object;
        int sectorIndex = (int) input.right;
        List<Integer> splits = ((LapInfo) input.left).getSplits();

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (sectorIndex < splits.size()) {
            if (splits.get(sectorIndex) != Integer.MAX_VALUE) {
                text = TimeUtils.asSeconds(splits.get(sectorIndex));
                if (Objects.equals(splits.get(sectorIndex), getSessionBestSectors().get(sectorIndex))) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    };

    private final LPTable.CellRenderer lapsRenderer = (
            PApplet applet,
            RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(String.valueOf(car.getRealtime().getLaps()), context.width / 2, context.height / 2);
    };

}
