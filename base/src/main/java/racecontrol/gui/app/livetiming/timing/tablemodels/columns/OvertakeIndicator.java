/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static racecontrol.client.extension.statistics.CarStatistics.OVERTAKE_INDICATOR;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.model.Car;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class OvertakeIndicator
        extends LPTableColumn {

    public OvertakeIndicator() {
        super("");
        setMinWidth(40);
        setMaxWidth(40);
        setCellRenderer(OvertakeIndicator::overtakeRenderer);
    }

    public static void overtakeRenderer(PApplet applet,
            LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        int overtake = stats.get(OVERTAKE_INDICATOR);
        if (overtake != 0) {
            int size = 10 * (int) Math.signum(overtake);
            float x = context.width / 2f;
            float y = context.height / 2f + size / 2f;
            if (overtake < 0) {
                applet.stroke(COLOR_RACE);
            } else {
                applet.stroke(COLOR_RED);
            }
            applet.strokeWeight(3);
            applet.line(x, y, x + size, y - size);
            applet.line(x, y, x - size, y - size);
            applet.strokeWeight(1);
            applet.noStroke();
        }
    }

}
