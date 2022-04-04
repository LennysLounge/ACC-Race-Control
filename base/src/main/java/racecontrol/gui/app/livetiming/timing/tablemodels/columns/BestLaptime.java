/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
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
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        int bestLapTime = car.sessionBestLap.getLapTimeMS();
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);
        if (bestLapTime == sessionbestLapTime) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
