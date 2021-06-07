/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

import racecontrol.client.data.CarInfo;
import racecontrol.utility.TimeUtils;
import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_GT4;
import static racecontrol.visualisation.LookAndFeel.COLOR_PORSCHE_CUP;
import static racecontrol.visualisation.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.visualisation.LookAndFeel.COLOR_WHITE;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import racecontrol.visualisation.gui.LPTableColumn;
import racecontrol.visualisation.gui.LPTable;
import racecontrol.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;

/**
 *
 * @author Leonard
 */
public class IncidentTableModel extends TableModel {

    private List<IncidentInfo> accidents = new LinkedList<>();

    @Override
    public int getRowCount() {
        return accidents.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("#")
            .setMaxWidth(LookAndFeel.LINE_HEIGHT)
            .setMinWidth(LookAndFeel.LINE_HEIGHT),
            new LPTableColumn("Session Time")
            .setMaxWidth(200)
            .setMinWidth(200),
            new LPTableColumn("Replay Time")
            .setMaxWidth(200)
            .setMinWidth(200),
            new LPTableColumn("Cars Involved")
            .setCellRenderer(carsRenderer)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        IncidentInfo a = accidents.get(accidents.size() - row - 1);
        switch (column) {
            case 0:
                return String.valueOf(accidents.size() - row - 1);
            case 1:
                return TimeUtils.asDuration(a.getSessionEarliestTime());
            case 2:
                if (a.getReplayTime() != 0) {
                    return TimeUtils.asDuration(a.getReplayTime());
                }
                return "-";
            case 3:
                return a.getCars();
        }
        return "-";
    }

    private final LPTable.CellRenderer carsRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        //Draw car numbres
        List<CarInfo> cars = (List<CarInfo>) context.object;
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
            x += w;
        }
    };

    public void setAccidents(List<IncidentInfo> accidents) {
        this.accidents = accidents;
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

    private enum CarType {
        GT3,
        GT4,
        ST,
        CUP;
    }

}
