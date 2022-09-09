/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.client.model.Car;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class LapCount
        extends LPTableColumn {

    public LapCount() {
        super("Laps");
        setMinWidth(70);
        setMaxWidth(70);
        setPriority(1);
        setCellRenderer(LapCount::lapCountRenderer);
    }

    public static void lapCountRenderer(PApplet applet,
            LPTable.RenderContext context) {
        Car car = (Car) context.object;
        String text = String.valueOf(car.lapCount);
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
