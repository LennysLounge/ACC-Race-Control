/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static racecontrol.client.extension.statistics.CarProperties.MAX_SPEED_TRAP_SPEED;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_COUNT_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME;
import static racecontrol.client.extension.statistics.CarProperties.PITLANE_TIME_STATIONARY;
import static racecontrol.client.extension.statistics.CarProperties.PLACES_GAINED;
import static racecontrol.client.extension.statistics.CarProperties.RACE_START_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPTable.RenderContext;
import racecontrol.gui.lpui.LPTableColumn;
import static racecontrol.client.extension.statistics.CarProperties.RACE_START_POSITION_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.SPEED_TRAP_SPEED;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class StatsTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("+/-")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> placesLostGainedRenderer(applet, context)),
            new LPTableColumn("Grid")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> startingPositionRenderer(applet, context)),
            new LPTableColumn("Pit Count")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> pitCountRenderer(applet, context)),
            new LPTableColumn("t/pit")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> pitTimeRenderer(applet, context)),
            new LPTableColumn("t/stopped")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> pitTimeStationaryRenderer(applet, context)),
            new LPTableColumn("Speed Trap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> speedTrapRenderer(applet, context))
        };
    }

    @Override
    public String getName() {
        return "Statistics";
    }

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(REALTIME_POSITION).compareTo(c2.get(REALTIME_POSITION))
                )
                .collect(toList());
    }

    private void placesLostGainedRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int placesGained = stats.get(PLACES_GAINED);
        if (placesGained != 0) {
            int size = 10 * (int) Math.signum(placesGained);
            float x = context.width / 2f - 15;
            float y = context.height / 2f + size / 2f;
            if (placesGained < 0) {
                applet.stroke(COLOR_RACE);
                applet.fill(COLOR_RACE);
            } else {
                applet.stroke(COLOR_RED);
                applet.fill(COLOR_RED);
            }
            applet.strokeWeight(3);
            applet.line(x, y, x + size, y - size);
            applet.line(x, y, x - size, y - size);
            applet.strokeWeight(1);
            applet.noStroke();

            String text = String.valueOf(Math.abs(placesGained))
                    + (stats.get(RACE_START_POSITION_ACCURATE) ? "" : "*");
            applet.textAlign(LEFT, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f + 5, context.height / 2f);
        }
    }

    private void startingPositionRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int startPos = stats.get(RACE_START_POSITION);
        if (startPos != 0) {
            String text = String.valueOf(startPos)
                    + (stats.get(RACE_START_POSITION_ACCURATE) ? "" : "*");
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void pitCountRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        String text = String.valueOf(stats.get(PITLANE_COUNT))
                + (stats.get(PITLANE_COUNT_ACCURATE) ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void pitTimeRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        if (stats.get(PITLANE_TIME) > 0) {
            String text = TimeUtils.asDurationShort(stats.get(PITLANE_TIME));
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void pitTimeStationaryRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        if (stats.get(PITLANE_TIME) > 0) {
            String text = TimeUtils.asDurationShort(stats.get(PITLANE_TIME_STATIONARY));
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void speedTrapRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        if (stats.get(SPEED_TRAP_SPEED) > 0) {
            String text = String.format("%d kmh", stats.get(SPEED_TRAP_SPEED));
            if (stats.get(SPEED_TRAP_SPEED).equals(stats.get(MAX_SPEED_TRAP_SPEED))) {
                applet.fill(COLOR_PURPLE);
            } else {
                applet.fill(COLOR_WHITE);
            }
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

}
