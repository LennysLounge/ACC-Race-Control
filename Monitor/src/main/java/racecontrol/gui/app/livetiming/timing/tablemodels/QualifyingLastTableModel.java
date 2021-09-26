/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.LAST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_TWO;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPTable;
import racecontrol.gui.lpui.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class QualifyingLastTableModel
        extends QualifyingBestTableModel {

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
            new LPTableColumn("Lap")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lapTimeRenderer(applet, context)),
            new LPTableColumn("Delta")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> deltaRenderer(applet, context)),
            new LPTableColumn("Last")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lastLapRenderer(applet, context)),
            new LPTableColumn("S1")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lastSectorOneRenderer(applet, context)),
            new LPTableColumn("S2")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lastSectorTwoRenderer(applet, context)),
            new LPTableColumn("S3")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lastSectorThreeRenderer(applet, context)),
            new LPTableColumn("Laps")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> lapsRenderer(applet, context))
        };
    }

    @Override
    public String getName() {
        return "Qualifying Last";
    }

    protected void lastLapRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int lastLapTime = stats.get(LAST_LAP_TIME);
        int bestLapTime = stats.get(BEST_LAP_TIME);
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);

        if (stats.get(LAST_LAP_INVALID)) {
            applet.fill(COLOR_RED);
        } else if (lastLapTime == sessionbestLapTime) {
            applet.fill(COLOR_PURPLE);
        } else if (lastLapTime <= bestLapTime) {
            applet.fill(COLOR_RACE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        if (lastLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(lastLapTime);
        }
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void lastSectorOneRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(LAST_SECTOR_ONE);
        int bestSplitTime = stats.get(BEST_SECTOR_ONE);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_ONE);

        if (splitTime > 0) {
            String text = TimeUtils.asSeconds(splitTime);
            if (splitTime > 999999) {
                text = "999.999";
            }
            applet.fill(COLOR_WHITE);
            if (!stats.get(LAST_LAP_INVALID)) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2, context.height / 2);
        }
    }

    protected void lastSectorTwoRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(LAST_SECTOR_TWO);
        int bestSplitTime = stats.get(BEST_SECTOR_TWO);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_TWO);

        if (splitTime > 0) {
            String text = TimeUtils.asSeconds(splitTime);
            if (splitTime > 999999) {
                text = "999.999";
            }
            if (!stats.get(LAST_LAP_INVALID)) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2, context.height / 2);
        }
    }

    protected void lastSectorThreeRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int splitTime = stats.get(LAST_SECTOR_THREE);
        int bestSplitTime = stats.get(BEST_SECTOR_THREE);
        int sessionBestSplitTime = stats.get(SESSION_BEST_SECTOR_THREE);

        if (splitTime > 0) {
            String text = TimeUtils.asSeconds(splitTime);
            if (splitTime > 999999) {
                text = "999.999";
            }
            applet.fill(COLOR_WHITE);
            if (!stats.get(LAST_LAP_INVALID)) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2, context.height / 2);
        }
    }
}
