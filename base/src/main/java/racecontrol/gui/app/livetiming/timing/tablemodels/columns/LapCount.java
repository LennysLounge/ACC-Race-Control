/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
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
        String text = String.valueOf(((CarStatistics) context.object).get(LAP_COUNT));
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
