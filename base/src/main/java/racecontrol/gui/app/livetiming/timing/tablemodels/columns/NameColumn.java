/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.client.model.Car;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_LAPPED;
import static racecontrol.gui.LookAndFeel.COLOR_LAPPING;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class NameColumn
        extends LPTableColumn {

    public NameColumn() {
        super("Name");
        setMaxWidth(LINE_HEIGHT * 5f);
        setMinWidth(LINE_HEIGHT * 5f);
        setPriority(1000);
        setCellRenderer(NameColumn::defaultNameRenderer);
    }

    public static void defaultNameRenderer(PApplet applet, LPTable.RenderContext context) {
        nameRenderer(applet, context, false, false);
    }

    public static void nameRenderer(PApplet applet,
            LPTable.RenderContext context,
            boolean isLapped,
            boolean isLapping) {
        Car car = (Car) context.object;

        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(1, 1, context.width - 1, context.height - 2);
        }

        applet.fill(COLOR_WHITE);
        if (isLapped) {
            applet.fill(COLOR_LAPPED);
        }
        if (isLapping) {
            applet.fill(COLOR_LAPPING);
        }
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(car.getDriver().truncatedName(), context.height / 4f,
                context.height / 2f);
    }

}
