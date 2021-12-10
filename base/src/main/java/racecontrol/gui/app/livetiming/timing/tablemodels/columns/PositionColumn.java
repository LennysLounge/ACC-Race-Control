/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarProperties.IS_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class PositionColumn
        extends LPTableColumn {

    public PositionColumn() {
        super("Pos");
        setMinWidth((int) (LINE_HEIGHT * 1.2f));
        setMaxWidth((int) (LINE_HEIGHT * 1.2f));
        setPriority(1000);
        setCellRenderer(this::positionRenderer);
    }

    protected void positionRenderer(PApplet applet, LPTable.RenderContext context) {
        if (!(context.object instanceof CarStatistics)) {
            return;
        }
        CarStatistics stats = (CarStatistics) context.object;

        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (stats.get(IS_SESSION_BEST)) {
            bgColor = LookAndFeel.COLOR_PURPLE;
            fgColor = LookAndFeel.COLOR_WHITE;
        } else if (stats.get(IS_FOCUSED_ON)) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, context.width - 2, context.height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(stats.get(REALTIME_POSITION)),
                context.width / 2f, context.height / 2f);
    }

}
