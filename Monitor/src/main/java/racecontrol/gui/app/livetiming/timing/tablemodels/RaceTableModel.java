/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import racecontrol.gui.lpui.LPTableColumn;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPTable.RenderContext;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class RaceTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Gap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapRenderer(applet, context)),
            new LPTableColumn("To Leader")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapToLeaderRenderer(applet, context)),
            new LPTableColumn("Lap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lapTimeRenderer(applet, context)),
            new LPTableColumn("Last")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lastLapRenderer(applet, context)),
            new LPTableColumn("Best")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> bestLapRenderer(applet, context)),
            new LPTableColumn("Laps")
            .setMaxWidth(70)
            .setCellRenderer((applet, context) -> lapCountRenderer(applet, context))
        };
    }

    @Override
    public String getName() {
        return "Race";
    }

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(POSITION).compareTo(c2.get(POSITION))
                )
                .collect(toList());
    }

    private void gapRenderer(PApplet applet, RenderContext context) {
        /*
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
         */
    }

    private void gapToLeaderRenderer(PApplet applet, RenderContext context) {
        /*
        LiveTimingEntry entry = ((LiveTimingEntry) context.object);

        String text = "--";
        if (entry.showLapsBehind()) {
            text = String.format("+%d Laps", entry.getLapsBehind());
        } else {
            if (entry.getGapToLeader() == 0) {
                text = "--";
            } else {
                text = TimeUtils.asGap(entry.getGapToLeader());
            }
        }

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(text, context.width - 20, context.height / 2);
         */
    }

    private void lapTimeRenderer(PApplet applet, RenderContext context) {
        /*
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
         */
    }

    private void lastLapRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int lastLapTime = stats.get(LAST_LAP_TIME);
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);
        if (lastLapTime == sessionbestLapTime) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        if (lastLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(lastLapTime);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void bestLapRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int bestLapTime = stats.get(BEST_LAP_TIME);
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);
        if (bestLapTime == sessionbestLapTime) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void lapCountRenderer(PApplet applet, RenderContext context) {
        String text = String.valueOf(((CarStatistics) context.object).get(LAP_COUNT));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }
}
