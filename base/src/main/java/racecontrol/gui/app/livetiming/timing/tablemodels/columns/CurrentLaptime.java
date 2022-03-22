/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
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
        CarStatistics stats = (CarStatistics) context.object;

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (stats.get(CAR_LOCATION) == TRACK) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (stats.get(CURRENT_LAP_INVALID)) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(stats.get(CURRENT_LAP_TIME));
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
