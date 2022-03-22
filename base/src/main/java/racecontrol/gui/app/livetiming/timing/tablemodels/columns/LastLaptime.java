/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class LastLaptime
        extends LPTableColumn {

    public LastLaptime() {
        super("Last");
        setMinWidth(100);
        setMaxWidth(200);
        setPriority(4);
        setCellRenderer(LastLaptime::lastLapRenderer);
    }

    public static void lastLapRenderer(PApplet applet,
            LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int lastLapTime = stats.get(LAST_LAP_TIME);
        int bestLapTime = stats.get(BEST_LAP_TIME);
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);

        if (stats.get(LAST_LAP_INVALID)) {
            applet.fill(COLOR_RED);
        } else if (lastLapTime == sessionbestLapTime) {
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

}
