/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent.entries;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.extension.returntogarage.ReturnToGarageEvent;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.enums.CarCategory;
import static racecontrol.client.protocol.enums.CarCategory.CUP;
import static racecontrol.client.protocol.enums.CarCategory.GT3;
import static racecontrol.client.protocol.enums.CarCategory.ST;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_GT4;
import static racecontrol.gui.LookAndFeel.COLOR_PORSCHE_CUP;
import static racecontrol.gui.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;

/**
 * A race event for when a car returns to garage.
 *
 * @author Leonard
 */
public class ReturnToGarageEntry
        extends RaceEventEntry {

    /**
     * The RTG event.
     */
    private final ReturnToGarageEvent event;

    public ReturnToGarageEntry(ReturnToGarageEvent event) {
        super(event.getSessionId(),
                event.getSessionTime(),
                "RTG",
                true
        );
        setReplayTime(event.getReplayTime());
        this.event = event;
    }

    /**
     * Renderer that renders the info column.
     */
    LPTable.CellRenderer carInfoRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        //Draw car numbres
        ReturnToGarageEntry entry = (ReturnToGarageEntry) context.object;
        Car car = entry.event.getCar();
        float x = 0;
        String carNumber = String.valueOf(car.carNumber);
        int background_color = 0;
        int text_color = 0;
        switch (car.getDriver().category) {
            case BRONZE:
                background_color = LookAndFeel.COLOR_RED;
                text_color = LookAndFeel.COLOR_BLACK;
                break;
            case SILVER:
                background_color = LookAndFeel.COLOR_GRAY;
                text_color = LookAndFeel.COLOR_WHITE;
                break;
            case GOLD:
            case PLATINUM:
                background_color = LookAndFeel.COLOR_WHITE;
                text_color = LookAndFeel.COLOR_BLACK;
                break;
        }

        float w = LookAndFeel.LINE_HEIGHT * 1.25f;
        applet.fill(background_color);
        applet.rect(x + 1, 1, w - 2, context.height - 2);

        if (isCarConnected(car.id)) {
            //draw outline if the mouse if over this car
            if (context.isMouseOverColumn
                    && context.isMouseOverRow
                    && context.mouseX > x
                    && context.mouseX < x + w) {
                applet.fill(COLOR_DARK_RED);
                applet.rect(x + 1, 1, w - 2, 3);
                applet.rect(x + 1, context.height - 1, w - 2, -3);
                applet.rect(x + 1, 1, 3, context.height - 2);
                applet.rect(x + w - 1, 1, -3, context.height - 2);
            }
        }

        //render GT4 / Cup / Super trofeo corners.
        CarCategory cat = car.carModel.getCategory();
        if (cat != GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(x + w - 1, context.height - 1);
            applet.vertex(x + w - 1, context.height - LINE_HEIGHT * 0.55f);
            applet.vertex(x + w - LINE_HEIGHT * 0.55f, context.height - 1);
            applet.endShape(CLOSE);
            applet.stroke(0, 0, 0, 50);
            applet.line(context.width - 1, context.height - LINE_HEIGHT * 0.55f,
                    context.width - LINE_HEIGHT * 0.55f, context.height);
            applet.noStroke();
            if (cat == ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (cat == CUP) {
                applet.fill(COLOR_PORSCHE_CUP);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(x + w - 1, context.height - 1);
            applet.vertex(x + w - 1, context.height - LINE_HEIGHT * 0.4f);
            applet.vertex(x + w - LINE_HEIGHT * 0.4f, context.height - 1);
            applet.endShape(CLOSE);
        }

        applet.fill(text_color);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(carNumber), x + w / 2, context.height / 2f);

        //Draw car number darker if this car is not connected.
        if (!isCarConnected(car.id)) {
            applet.fill(0, 0, 0, 150);
            applet.rect(x + 1, 1, w - 2, context.height - 2);
        }
    };

    private boolean isCarConnected(int carId) {
        return getClient().getModel().cars.keySet().contains(carId);
    }

    @Override
    public LPTable.CellRenderer getInfoRenderer() {
        return carInfoRenderer;
    }

    @Override
    public String getInfo() {
        return event.getCar().carNumberString();
    }

}
