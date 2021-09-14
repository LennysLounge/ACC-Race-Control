/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.entries;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.LookAndFeel.COLOR_GT4;
import static racecontrol.LookAndFeel.COLOR_PORSCHE_CUP;
import static racecontrol.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.LookAndFeel.COLOR_WHITE;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.SessionId;
import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.lpgui.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class ContactEventEntry
        extends RaceEventEntry {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ContactEventEntry.class.getName());
    /**
     * The incident this entry represents.
     */
    private final ContactInfo incident;
    /**
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;

    public ContactEventEntry(
            SessionId sessionId,
            float sessionTime,
            String typeDesciptor,
            boolean hasReplay,
            ContactInfo incident) {
        super(sessionId, sessionTime, typeDesciptor, hasReplay);
        client = AccBroadcastingClient.getClient();
        this.incident = incident;
        setReplayTime(incident.getReplayTime());
    }

    /**
     * Renderer that renders the info column.
     */
    LPTable.CellRenderer carInfoRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        //Draw car numbres
        ContactEventEntry entry = (ContactEventEntry) context.object;
        List<CarInfo> cars = entry.incident.getCars();
        float x = 0;
        for (CarInfo car : cars) {
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
            applet.rect(x + 1, 1, w - 2, context.height - 2);

            if (isCarConnected(car.getCarId())) {
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
            CarType type = getCarType(car.getCarModelType());
            if (type != CarType.GT3) {
                applet.fill(COLOR_WHITE);
                applet.beginShape();
                applet.vertex(x + w - 1, context.height - 1);
                applet.vertex(x + w - 1, context.height - LINE_HEIGHT * 0.5f);
                applet.vertex(x + w - LINE_HEIGHT * 0.5f, context.height - 1);
                applet.endShape(CLOSE);
                if (type == CarType.ST) {
                    applet.fill(COLOR_SUPER_TROFEO);
                } else if (type == CarType.CUP) {
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
            if (!isCarConnected(car.getCarId())) {
                applet.fill(0, 0, 0, 150);
                applet.rect(x + 1, 1, w - 2, context.height - 2);
            }

            x += w;
        }
    };

    @Override
    public LPTable.CellRenderer getInfoRenderer() {
        return carInfoRenderer;
    }

    /**
     * Triggers the action for when the info cell was clicked.
     *
     * @param x the x position of the mouse when clicked.
     * @param y the y position of the mouse when clicked.
     */
    public void onInfoClicked(int x, int y) {
        //Car column clicked
        int index = (int) (x / (LINE_HEIGHT * 1.25f));
        if (index < incident.getCars().size()) {
            client.sendChangeFocusRequest(incident.getCars().get(index).getCarId());
        }
    }

    private boolean isCarConnected(int carId) {
        return client.getModel().getCarsInfo().keySet().contains(carId);
    }

    private CarType getCarType(byte carModelId) {
        switch (carModelId) {
            case 9:
                return CarType.CUP;
            case 18:
                return CarType.ST;
            case 50:
            case 51:
            case 52:
            case 53:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
                return CarType.GT4;
            default:
                return CarType.GT3;

        }
    }

    @Override
    public String getInfo() {
        return incident.getCars().stream()
                .map(car -> "#" + car.getCarNumber())
                .collect(Collectors.joining(", "));
    }

    private enum CarType {
        GT3,
        GT4,
        ST,
        CUP;
    }
}
