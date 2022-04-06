/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.client.model.Car;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class TestTableModel
        extends QualifyingBestTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new LPTableColumn("Pos")
            .setMaxWidth(100)
            .setCellRenderer(this::r1),
            new LPTableColumn("track pos")
            .setMaxWidth(100)
            .setCellRenderer(this::r2),
            new LPTableColumn("spline pos")
            .setMaxWidth(100)
            .setCellRenderer(this::r3)/*,
            new LPTableColumn("Count")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r4(applet, context)),
            new LPTableColumn("OI")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r5(applet, context)),
            new LPTableColumn("S3C")
            .setMaxWidth(100)
            .setCellRenderer((applet, context) -> r6(applet, context))
         */
        };
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((car1, car2) -> Integer.compare(car1.realtimePosition, car2.realtimePosition))
                .collect(toList());
    }

    private void r1(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;

        String text = String.format("%.5f", car.position * 1f);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r2(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = String.format("%.5f", car.trackPosition * 1f);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

    private void r3(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = String.format("%.5f", car.splinePosition);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, context.width / 2f, context.height / 2f);
    }

}
