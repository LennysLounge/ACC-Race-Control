/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.model.Car;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LapCount;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LastLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
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
            new LastLaptime(),
            new LPTableColumn("S1")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> lastSectorOneRenderer(applet, context)),
            new LPTableColumn("S2")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> lastSectorTwoRenderer(applet, context)),
            new LPTableColumn("S3")
            .setMinWidth(80)
            .setPriority(0)
            .setCellRenderer((applet, context) -> lastSectorThreeRenderer(applet, context)),
            new LapCount()
        };
    }

    @Override
    public String getName() {
        return "Qualifying Last";
    }

    protected void lastLapRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        int lastLapTime = car.lastLap.getLapTimeMS();
        int bestLapTime = car.bestLap.getLapTimeMS();

        if (car.lastLap.isInvalid()) {
            applet.fill(COLOR_RED);
        } else if (car.isSessionBestLaptime) {
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
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        int splitTime = car.lastLap.getSplits().get(0);
        int bestSplitTime = car.bestLap.getSplits().get(0);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(0);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime > 0 && splitTime < 999999) {
            text = TimeUtils.asSeconds(splitTime);
            if (!car.lastLap.isInvalid()) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }

        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void lastSectorTwoRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        int splitTime = car.lastLap.getSplits().get(0);
        int bestSplitTime = car.bestLap.getSplits().get(0);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(1);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime > 0 && splitTime < 999999) {
            text = TimeUtils.asSeconds(splitTime);
            if (!car.lastLap.isInvalid()) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void lastSectorThreeRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        int splitTime = car.lastLap.getSplits().get(0);
        int bestSplitTime = car.bestLap.getSplits().get(0);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(2);

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (splitTime > 0 && splitTime < 999999) {
            text = TimeUtils.asSeconds(splitTime);
            if (!car.lastLap.isInvalid()) {
                if (splitTime <= bestSplitTime) {
                    applet.fill(COLOR_RACE);
                }
                if (splitTime <= sessionBestSplitTime) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }
}
