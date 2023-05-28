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
import racecontrol.client.model.Car;
import racecontrol.client.model.Driver;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
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
public class DriversTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn()
            .setMaxWidth(LINE_HEIGHT * 7f)
            .setMinWidth(LINE_HEIGHT * 7f)
            .setPriority(1000)
            .setCellRenderer(this::nameRenderer),
            new LPTableColumn("Category")
            .setMaxWidth(100)
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setCellRenderer((applet, context) -> driverCategoryRenderer(applet, context)),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("Class")
            .setMaxWidth(100)
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setCellRenderer((applet, context) -> carClassRenderer(applet, context)),
            new LPTableColumn("Car")
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setGrowthRate(2)
            .setCellRenderer((applet, context) -> carRenderer(applet, context)),
            new LPTableColumn("Stint")
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setGrowthRate(1)
            .setCellRenderer((applet, context) -> stintTimeRenderer(applet, context))
        };
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((car1, car2) -> Integer.compare(car1.realtimePosition, car2.realtimePosition))
                .collect(toList());
    }

    @Override
    public String getName() {
        return "Drivers";
    }

    @Override
    public float getRowHeight(int rowIndex) {
        Car car = getEntry(rowIndex);
        if (car == null) {
            return LINE_HEIGHT;
        }
        return LINE_HEIGHT * (car.drivers.size() - 1) * 0.6f + LINE_HEIGHT;
    }

    protected void nameRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        float y = LINE_HEIGHT / 2f;
        for (Driver driver : car.drivers) {
            String name = driver.fullName();
            applet.fill(COLOR_WHITE);
            applet.textAlign(LEFT, CENTER);
            if (car.getDriver() == driver) {
                applet.textFont(LookAndFeel.fontMedium());
                if (car.drivers.size() > 1) {
                    name = "<" + name + ">";
                }
            } else {
                applet.textFont(LookAndFeel.fontRegular());
            }
            applet.text(name, 10f, y);
            y += LINE_HEIGHT * 0.6f;
        }
    }

    private void driverCategoryRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        int i = 0;
        for (Driver driver : car.drivers) {
            String name = driver.category.getText();
            applet.fill(COLOR_WHITE);
            applet.textAlign(LEFT, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(name, 10f, LINE_HEIGHT / 2f + (LINE_HEIGHT * 0.6f) * i);
            i++;
        }
    }

    private void carClassRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String name = car.carModel.getCategory().getText();
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(name, 10f, LINE_HEIGHT / 2f);
    }

    private void carRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String name = car.carModel.getName();
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(name, 10f, LINE_HEIGHT / 2f);
    }

    private void stintTimeRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = TimeUtils.asDurationShort(car.driverStintTime);
        text = text + (car.driverStintTimeAccurate ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, 10f, context.height / 2f);
    }
}
