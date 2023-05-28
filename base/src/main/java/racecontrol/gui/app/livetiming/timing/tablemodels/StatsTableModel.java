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
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable.RenderContext;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.client.model.Car;
import racecontrol.client.model.Model;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
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
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("+/-")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> placesLostGainedRenderer(applet, context)),
            new LPTableColumn("Grid")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> startingPositionRenderer(applet, context)),
            new LPTableColumn("Pit Count")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> pitCountRenderer(applet, context)),
            new LPTableColumn("t/pit")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> pitTimeRenderer(applet, context)),
            new LPTableColumn("t/stopped")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> pitTimeStationaryRenderer(applet, context)),
            new LPTableColumn("Speed Trap")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> speedTrapRenderer(applet, context)),
            new LPTableColumn("Stint")
            .setMinWidth(100)
            .setCellRenderer((applet, context) -> stintTimeRenderer(applet, context))
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
                .sorted((car1, car2) -> Integer.compare(car1.realtimePosition, car2.realtimePosition))
                .collect(toList());
    }

    private void placesLostGainedRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        int placesLost = car.realtimePosition - car.raceStartPosition;
        int size = 10 * (int) Math.signum(placesLost);
        float x = context.width / 2f - 15;
        float y = context.height / 2f + size / 2f;
        applet.strokeWeight(3);
        if (placesLost != 0) {
            if (placesLost < 0) {
                applet.stroke(COLOR_RACE);
                applet.fill(COLOR_RACE);
            } else {
                applet.stroke(COLOR_RED);
                applet.fill(COLOR_RED);
            }
            applet.line(x, y, x + size, y - size);
            applet.line(x, y, x - size, y - size);
        } else {
            applet.stroke(COLOR_WHITE);
            applet.fill(COLOR_WHITE);
        }
        applet.strokeWeight(1);
        applet.noStroke();

        String text = String.valueOf(Math.abs(placesLost))
                + (car.raceStartPositionAccurate ? "" : "*");
        if (placesLost == 0) {
            text = "--";
        }
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f + 5, context.height / 2f);
    }

    private void startingPositionRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        int startPos = car.raceStartPosition;
        String text = String.valueOf(startPos)
                + (car.raceStartPositionAccurate ? "" : "*");
        if (startPos == 0) {
            text = "--";
        }
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void pitCountRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        String text = String.valueOf(car.pitlaneCount)
                + (car.pitlaneCountAccurate ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void pitTimeRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        if (car.pitLaneTime > 0) {
            String text = TimeUtils.asDurationShort(car.pitLaneTime);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void pitTimeStationaryRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        if (car.pitLaneTime > 0) {
            String text = TimeUtils.asDurationShort(car.pitLaneTimeStationary);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(text, context.width / 2f, context.height / 2f);
        }
    }

    private void speedTrapRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        Model model = getClient().getModel();
        String text = "--";
        if (car.speedTrapKMH > 0) {
            text = String.format("%d kmh", car.speedTrapKMH);
        }
        if (car.speedTrapKMH == model.session.maxSpeedTrapKMH) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void stintTimeRenderer(PApplet applet, RenderContext context) {
        Car car = (Car) context.object;
        String text = TimeUtils.asDurationShort(car.driverStintTime);
        text = text + (car.driverStintTimeAccurate ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2f, context.height / 2f);
    }

}
