/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.AccBroadcastingClient.getClient;
import static racecontrol.client.protocol.enums.CarLocation.TRACK;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.client.model.Car;
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
                .sorted((c1, c2) -> Integer.compare(c1.position, c2.position))
                .collect(toList());
    }

    protected void lapTimeRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = "--";
        applet.fill(COLOR_WHITE);
        if (car.carLocation == TRACK) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (car.currentLap.isInvalid()) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(car.currentLap.getLapTimeMS());
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void deltaRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = "--";
        if (car.carLocation == TRACK) {
            applet.fill(LookAndFeel.COLOR_RACE);
            if (car.delta > 0) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asDelta(car.delta);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    protected void bestSectorOneRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        int splitTime = car.bestLap.getSplits().get(0);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(0);

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
        Car car = (Car) context.object;
        int splitTime = car.bestLap.getSplits().get(1);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(1);

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
        Car car = (Car) context.object;
        int splitTime = car.bestLap.getSplits().get(2);
        int sessionBestSplitTime = getClient().getModel().session.sessionBestSplits.get(2);

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
