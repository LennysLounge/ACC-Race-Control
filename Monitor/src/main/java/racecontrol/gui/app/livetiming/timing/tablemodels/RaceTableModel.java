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
import static processing.core.PConstants.RIGHT;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.IS_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_ORANGE;
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
        CarStatistics stats = (CarStatistics) context.object;
        int gap = stats.get(GAP_TO_POSITION_AHEAD);
        String text = TimeUtils.asGap(gap);
        if (stats.get(POSITION) == 1) {
            text = "--";
        }
        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        if (gap < 1000 && gap > 0) {
            applet.fill(COLOR_ORANGE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.text(text, context.width - 20, context.height / 2);
    }

    private void gapToLeaderRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        String text = "--";
        if (stats.get(LAPS_BEHIND_SPLIT)) {
            text = String.format("+%d Laps", stats.get(LAPS_BEHIND_LEADER));
        } else {
            if (stats.get(GAP_TO_LEADER) == 0) {
                text = "--";
            } else {
                text = TimeUtils.asGap(stats.get(GAP_TO_LEADER));
            }
        }
        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(text, context.width - 20, context.height / 2);

    }

    private void lapTimeRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (stats.get(CAR_LOCATION) == TRACK) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (stats.get(IS_LAP_INVALID)) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(stats.get(CURRENT_LAP_TIME));
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
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
