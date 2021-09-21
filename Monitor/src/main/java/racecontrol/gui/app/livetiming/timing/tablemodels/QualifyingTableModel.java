/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.DELTA;
import static racecontrol.client.extension.statistics.CarProperties.IS_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAP_TIME_GAP_TO_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import racecontrol.gui.lpui.LPTable;
import racecontrol.gui.lpui.LPTableColumn;
import racecontrol.utility.TimeUtils;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_TWO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;

/**
 *
 * @author Leonard
 */
public class QualifyingTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Lap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lapTimeRenderer(applet, context)),
            new LPTableColumn("Delta")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> deltaRenderer(applet, context)),
            new LPTableColumn("Best")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> bestLapRenderer(applet, context)),
            new LPTableColumn("Gap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> gapRenderer(applet, context)),
            new LPTableColumn("S1")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> sectorOneRenderer(applet, context)),
            new LPTableColumn("S2")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> sectorTwoRenderer(applet, context)),
            new LPTableColumn("S3")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> sectorThreeRenderer(applet, context)),
            new LPTableColumn("Laps")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lapsRenderer(applet, context))
        };
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

    private void lapTimeRenderer(PApplet applet, LPTable.RenderContext context) {
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

    private void deltaRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = "--";
        if (stats.get(CAR_LOCATION) == TRACK) {

            applet.fill(LookAndFeel.COLOR_RACE);
            if (stats.get(DELTA) > 0) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asDelta(stats.get(DELTA));
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void bestLapRenderer(PApplet applet, LPTable.RenderContext context) {
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
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void gapRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        String text = "--";
        if (stats.get(BEST_LAP_TIME) != Integer.MAX_VALUE
                && stats.get(LAP_TIME_GAP_TO_SESSION_BEST) != 0) {
            text = TimeUtils.asDelta(stats.get(LAP_TIME_GAP_TO_SESSION_BEST));
        }
        applet.noStroke();
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void sectorOneRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(BEST_SECTOR_ONE);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_ONE);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime != Integer.MAX_VALUE) {
            text = TimeUtils.asSeconds(splitTime);
            if (splitTime == sessionBestSplitTime) {
                applet.fill(COLOR_PURPLE);
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void sectorTwoRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(BEST_SECTOR_TWO);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_TWO);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime != Integer.MAX_VALUE) {
            text = TimeUtils.asSeconds(splitTime);
            if (splitTime == sessionBestSplitTime) {
                applet.fill(COLOR_PURPLE);
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void sectorThreeRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(BEST_SECTOR_THREE);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_THREE);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime != Integer.MAX_VALUE) {
            text = TimeUtils.asSeconds(splitTime);
            if (splitTime == sessionBestSplitTime) {
                applet.fill(COLOR_PURPLE);
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void lapsRenderer(PApplet applet, LPTable.RenderContext context) {
        String text = String.valueOf(((CarStatistics) context.object).get(LAP_COUNT));
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(text, context.width / 2, context.height / 2);
    }

    @Override
    public String getName() {
        return "Qualifying";
    }

}
