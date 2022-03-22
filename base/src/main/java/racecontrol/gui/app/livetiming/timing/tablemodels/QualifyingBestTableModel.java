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
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.DELTA;
import static racecontrol.client.extension.statistics.CarProperties.POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_SECTOR_TWO;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_ONE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_THREE;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_SECTOR_TWO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_INVALID;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.BestLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LapCount;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;

/**
 *
 * @author Leonard
 */
public class QualifyingBestTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("Gap")
            .setMinWidth(90)
            .setPriority(3)
            .setCellRenderer((applet, context) -> gapRenderer(applet, context)),
            new LPTableColumn("Lap")
            .setMinWidth(100)
            .setPriority(1)
            .setCellRenderer((applet, context) -> lapTimeRenderer(applet, context)),
            new LPTableColumn("Delta")
            .setMinWidth(100)
            .setPriority(2)
            .setCellRenderer((applet, context) -> deltaRenderer(applet, context)),
            new BestLaptime(),
            new LPTableColumn("BS1")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> bestSectorOneRenderer(applet, context)),
            new LPTableColumn("BS2")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> bestSectorTwoRenderer(applet, context)),
            new LPTableColumn("BS3")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> bestSectorThreeRenderer(applet, context)),
            new LapCount()
        };
    }

    @Override
    public String getName() {
        return "Qualifying Best";
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(POSITION).compareTo(c2.get(POSITION))
                )
                .collect(toList());
    }

    protected void lapTimeRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (stats.get(CAR_LOCATION) == TRACK) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (stats.get(CURRENT_LAP_INVALID)) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(stats.get(CURRENT_LAP_TIME));
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void deltaRenderer(PApplet applet, LPTable.RenderContext context) {
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

    protected void bestSectorOneRenderer(PApplet applet, LPTable.RenderContext context) {
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

    protected void bestSectorTwoRenderer(PApplet applet, LPTable.RenderContext context) {
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

    protected void bestSectorThreeRenderer(PApplet applet, LPTable.RenderContext context) {
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
}
