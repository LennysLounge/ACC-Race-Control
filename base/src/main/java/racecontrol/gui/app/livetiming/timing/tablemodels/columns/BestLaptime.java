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
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class BestLaptime
        extends LPTableColumn {

    public BestLaptime() {
        super("Best");
        setMinWidth(100);
        setMaxWidth(200);
        setPriority(4);
        setCellRenderer(BestLaptime::bestLapRenderer);
    }

    public static void bestLapRenderer(PApplet applet,
            LPTable.RenderContext context) {
        Car car = (Car) context.object;
        if (car.isSessionBestLaptime) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        int bestLapTime = car.bestLap.getLapTimeMS();
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
