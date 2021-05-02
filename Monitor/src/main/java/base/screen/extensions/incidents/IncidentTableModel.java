/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents;

import base.screen.networking.data.CarInfo;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.gui.LPTableColumn;
import base.screen.visualisation.gui.LPTable;
import base.screen.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class IncidentTableModel extends TableModel {

    private final LPTable.CellRenderer carsRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        //Draw car numbres
        List<CarInfo> cars = (List<CarInfo>) object;
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
            applet.rect(x + 1, 1, w - 2, height - 2);
            applet.fill(text_color);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontMedium());
            applet.text(String.valueOf(carNumber), x + w / 2, height / 2f);
            x += w;
        }
    };

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
                return a.getCars();
        }
        return "-";
    }

    public void setAccidents(List<IncidentInfo> accidents) {
        this.accidents = accidents;
    }

}
