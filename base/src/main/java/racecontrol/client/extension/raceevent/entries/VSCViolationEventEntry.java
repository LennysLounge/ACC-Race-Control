/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent.entries;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import static processing.core.PConstants.RIGHT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_GT4;
import static racecontrol.gui.LookAndFeel.COLOR_PORSCHE_CUP;
import static racecontrol.gui.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.client.extension.vsc.events.VSCViolationEvent;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.SessionId;
import racecontrol.client.data.enums.CarCategory;
import static racecontrol.client.data.enums.CarCategory.CUP;
import static racecontrol.client.data.enums.CarCategory.GT3;
import static racecontrol.client.data.enums.CarCategory.ST;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class VSCViolationEventEntry
        extends RaceEventEntry {

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;
    /**
     * The violation event.
     */
    private final VSCViolationEvent violation;
    /**
     * The car info in the violation event.
     */
    private final CarInfo carInfo;

    public VSCViolationEventEntry(
            SessionId sessionId,
            int sessionTime,
            String typeDesciptor,
            boolean hasReplay,
            VSCViolationEvent info) {
        super(sessionId, sessionTime, typeDesciptor, hasReplay);
        this.client = AccBroadcastingClient.getClient();
        this.violation = info;
        this.carInfo = client.getBroadcastingData().getCar(violation.getCarId());
    }

    /**
     * Renderer that renders the info column.
     */
    LPTable.CellRenderer carInfoRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        //Draw car number
        VSCViolationEventEntry entry = (VSCViolationEventEntry) context.object;
        CarInfo car = entry.carInfo;
        String carNumber = String.valueOf(car.getCarNumber());
        int background_color = 0;
        int text_color = 0;
        switch (car.getDriver().getCategory()) {
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
        applet.rect(1, 1, w - 2, context.height - 2);

        if (isCarConnected(car.getCarId())) {
            //draw outline if the mouse if over this car
            if (context.isMouseOverColumn
                    && context.isMouseOverRow
                    && context.mouseX > 0
                    && context.mouseX < w) {
                applet.fill(COLOR_DARK_RED);
                applet.rect(1, 1, w - 2, 3);
                applet.rect(1, context.height - 1, w - 2, -3);
                applet.rect(1, 1, 3, context.height - 2);
                applet.rect(w - 1, 1, -3, context.height - 2);
            }
        }

        //render GT4 / Cup / Super trofeo corners.
        CarCategory cat = car.getCarModel().getCategory();
        if (cat != GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(w - 1, context.height - 1);
            applet.vertex(w - 1, context.height - LINE_HEIGHT * 0.5f);
            applet.vertex(w - LINE_HEIGHT * 0.5f, context.height - 1);
            applet.endShape(CLOSE);
            if (cat == ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (cat == CUP) {
                applet.fill(COLOR_PORSCHE_CUP);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(w - 1, context.height - 1);
            applet.vertex(w - 1, context.height - LINE_HEIGHT * 0.4f);
            applet.vertex(w - LINE_HEIGHT * 0.4f, context.height - 1);
            applet.endShape(CLOSE);
        }

        applet.fill(text_color);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(carNumber), w / 2, context.height / 2f);

        //Draw car number darker if this car is not connected.
        if (!isCarConnected(car.getCarId())) {
            applet.fill(0, 0, 0, 150);
            applet.rect(1, 1, w - 2, context.height - 2);
        }

        // draw text next to number.
        VSCViolationEvent v = entry.violation;
        applet.fill(COLOR_WHITE);
        applet.noStroke();
        applet.textFont(LookAndFeel.fontRegular());
        applet.textAlign(RIGHT, CENTER);
        String speed = String.format("+%d kmh,", v.getSpeedOver());
        applet.text(speed, w + 110, LINE_HEIGHT / 2f);

        String time = TimeUtils.asDelta(v.getTimeOver());
        applet.text(time, w + 210, LINE_HEIGHT / 2f);

    };

    private boolean isCarConnected(int carId) {
        return client.getBroadcastingData().getCarsInfo().keySet().contains(carId);
    }

    /**
     * Triggers the action for when the info cell was clicked.
     *
     * @param x the x position of the mouse when clicked.
     * @param y the y position of the mouse when clicked.
     */
    public void onInfoClicked(int x, int y) {
        if (x < LINE_HEIGHT * 1.25f) {
            client.sendChangeFocusRequest(carInfo.getCarId());
        }
    }

    @Override
    public LPTable.CellRenderer getInfoRenderer() {
        return carInfoRenderer;
    }

    @Override
    public String getInfo() {
        return "#" + carInfo.getCarNumber()
                + String.format(" +%d kmh, +%.1f s",
                        violation.getSpeedOver(),
                        violation.getTimeOver() / 1000);
    }

}
