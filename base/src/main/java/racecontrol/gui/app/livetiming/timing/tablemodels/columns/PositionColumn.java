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
        Car car = (Car) context.object;

        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (car.isSessionBestLaptime) {
            bgColor = LookAndFeel.COLOR_PURPLE;
            fgColor = LookAndFeel.COLOR_WHITE;
        } else if (car.isFocused) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, context.width - 2, context.height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(car.realtimePosition),
                context.width / 2f, context.height / 2f);
    }

}
