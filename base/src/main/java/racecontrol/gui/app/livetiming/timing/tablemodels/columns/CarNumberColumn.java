/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import racecontrol.client.protocol.enums.CarCategory;
import static racecontrol.client.protocol.enums.CarCategory.CHL;
import static racecontrol.client.protocol.enums.CarCategory.CUP;
import static racecontrol.client.protocol.enums.CarCategory.CUP21;
import static racecontrol.client.protocol.enums.CarCategory.GT3;
import static racecontrol.client.protocol.enums.CarCategory.ST;
import static racecontrol.client.protocol.enums.CarCategory.ST22;
import static racecontrol.client.protocol.enums.CarCategory.TCX;
import racecontrol.client.model.Car;
import static racecontrol.client.protocol.enums.DriverCategory.BRONZE;
import static racecontrol.client.protocol.enums.DriverCategory.GOLD;
import static racecontrol.client.protocol.enums.DriverCategory.PLATINUM;
import static racecontrol.client.protocol.enums.DriverCategory.SILVER;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GT4;
import static racecontrol.gui.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class CarNumberColumn
        extends LPTableColumn {

    public CarNumberColumn() {
        super("#");
        setMinWidth(LINE_HEIGHT * 1.3f);
        setMaxWidth(LINE_HEIGHT * 1.3f);
        setPriority(1000);
        setCellRenderer(this::carNumberRenderer);
    }

    protected void carNumberRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;

        int backColor = 0;
        int frontColor = 0;
        switch (car.getDriver().category) {
            case BRONZE:
                backColor = LookAndFeel.COLOR_RED;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
            case SILVER:
                backColor = LookAndFeel.COLOR_DRIVER_SILVER;
                frontColor = LookAndFeel.COLOR_WHITE;
                break;
            case GOLD:
            case PLATINUM:
                backColor = LookAndFeel.COLOR_WHITE;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
        }
        applet.noStroke();
        applet.fill(backColor);
        applet.rect(0, 1, context.width, context.height - 2);

        //render GT4 / Cup / Super trofeo corners.
        CarCategory cat = car.carModel.getCategory();
        if (cat != GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(context.width, context.height - 1);
            applet.vertex(context.width, context.height - LINE_HEIGHT * 0.55f);
            applet.vertex(context.width - LINE_HEIGHT * 0.55f, context.height - 1);
            applet.endShape(CLOSE);
            applet.stroke(0, 0, 0, 50);
            applet.line(context.width, context.height - LINE_HEIGHT * 0.55f,
                    context.width - LINE_HEIGHT * 0.55f, context.height);
            applet.noStroke();
            if (cat == ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (cat == ST22) {
                applet.fill(LookAndFeel.COLOR_SUPER_TROFEO22);
            } else if (cat == CUP) {
                applet.fill(LookAndFeel.COLOR_PORSCHE_CUP);
            } else if (cat == CUP21) {
                applet.fill(LookAndFeel.COLOR_PORSCHE_CUP21);
            } else if (cat == CHL) {
                applet.fill(LookAndFeel.COLOR_FERRARI_CHALLENGE);
            } else if (cat == TCX) {
                applet.fill(LookAndFeel.COLOR_TCX);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(context.width, context.height - 1);
            applet.vertex(context.width, context.height - LINE_HEIGHT * 0.4f);
            applet.vertex(context.width - LINE_HEIGHT * 0.4f, context.height - 1);
            applet.endShape(CLOSE);
        }

        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(car.carNumber),
                context.width / 2f, context.height / 2f);
    }

}
