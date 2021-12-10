/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static racecontrol.client.extension.statistics.CarProperties.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarProperties.IS_YELLOW_FLAG;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_FINISHED;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_BLACK;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.COLOR_YELLOW;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class PitFlagColumn
        extends LPTableColumn {
    
    private boolean renderMouseOver = true;

    public PitFlagColumn() {
        super("");
        setMaxWidth((int) (LINE_HEIGHT * 0.4f));
        setMinWidth((int) (LINE_HEIGHT * 0.4f));
        setPriority(1000);
        setCellRenderer(this::pitRenderer);
    }
    
    public PitFlagColumn setRenderMouseOver(boolean state){
        renderMouseOver = state;
        return this;
    }

    protected void pitRenderer(PApplet applet, LPTable.RenderContext context) {
        if (!(context.object instanceof CarStatistics)) {
            return;
        }
        CarStatistics stats = (CarStatistics) context.object;
        if (context.isMouseOverRow && renderMouseOver) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, context.width - 1, context.height - 2);
        }
        if (stats.get(SESSION_FINISHED)) {
            applet.fill(COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            float w = (context.width - 2) / 2;
            float h = (context.height - 2) / 6;
            applet.fill(COLOR_BLACK);
            for (int i = 0; i < 6; i++) {
                applet.rect(1 + w * (i % 2), 1 + h * i, w, h);
            }
        } else if (stats.get(IS_IN_PITS)) {
            applet.noStroke();
            applet.fill(COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(TEXT_SIZE * 0.6f);
            applet.text("P", context.width / 2f, context.height / 2f);
            applet.textFont(LookAndFeel.fontMedium());
            applet.textSize(LookAndFeel.TEXT_SIZE);
        } else if (stats.get(IS_YELLOW_FLAG)) {
            applet.fill(COLOR_YELLOW);
            applet.rect(1, 1, context.width - 2, context.height - 2);
        }
    }

}
