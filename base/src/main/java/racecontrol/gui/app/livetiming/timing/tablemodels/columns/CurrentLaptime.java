/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.protocol.enums.CarLocation.TRACK;
import racecontrol.client.model.Car;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class CurrentLaptime
        extends LPTableColumn {

    public CurrentLaptime() {
        super("Lap");
        setMinWidth(100);
        setMaxWidth(200);
        setPriority(4);
        setCellRenderer(CurrentLaptime::currentLapRenderer);
    }

    public static void currentLapRenderer(PApplet applet,
            LPTable.RenderContext context) {
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

}
