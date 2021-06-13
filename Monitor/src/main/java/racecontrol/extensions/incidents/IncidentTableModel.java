/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.incidents;

import java.util.Arrays;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import static racecontrol.visualisation.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.visualisation.LookAndFeel.COLOR_GRAY;
import static racecontrol.visualisation.LookAndFeel.COLOR_PRACTICE;
import static racecontrol.visualisation.LookAndFeel.COLOR_QUALIFYING;
import static racecontrol.visualisation.LookAndFeel.COLOR_RACE;
import static racecontrol.visualisation.LookAndFeel.COLOR_RED;

/**
 *
 * @author Leonard
 */
public class IncidentTableModel extends TableModel {

    /**
     * This class's logger.
     */
    public static final Logger LOG = Logger.getLogger(IncidentTableModel.class.getName());
    /**
     * Maximum cars per row.
     */
    public static final int MAX_CARS_PER_ROW = 4;
    /**
     * List of incident entries to display.
     */
    private List<IncidentEntry> incidents = new LinkedList<>();
    /**
     * List of cars that are currently connected.
     */
    private List<Integer> currentlyConnectedCars;
    /**
     * Reference to the base extension object.
     */
    private final IncidentExtension extension;

    public IncidentTableModel(IncidentExtension extension) {
        this.extension = extension;
    }

    @Override
    public int getRowCount() {
        return incidents.stream()
                .collect(Collectors.summingInt(entry -> entry.getRows()));
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new LPTableColumn("#")
            .setMaxWidth(LINE_HEIGHT)
            .setMinWidth(LINE_HEIGHT),
            new LPTableColumn("Session Time")
            .setMaxWidth(200)
            .setMinWidth(200),
            new LPTableColumn("Replay Time")
            .setMaxWidth(200)
            .setMinWidth(200),
            new LPTableColumn("Cars Involved")
            .setMinWidth(LINE_HEIGHT * 1.25f * MAX_CARS_PER_ROW)
            .setMaxWidth(LINE_HEIGHT * 1.25f * MAX_CARS_PER_ROW)
            .setCellRenderer(carsRenderer),
            new LPTableColumn("Replay")
            .setMinWidth(LINE_HEIGHT * 6.5f)
            .setMaxWidth(LINE_HEIGHT * 6.5f)
            .setCellRenderer(replayButtonRenderer),
            new LPTableColumn("")
            .setMaxWidth(0)
            .setMinWidth(0)
            .setCellRenderer(dividerRender)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        IncidentEntry entry = null;
        int subRow = 0;
        //find correct Entry
        int rowCount = 0;
        for (int i = 0; i < incidents.size(); i++) {
            entry = incidents.get(i);
            rowCount += entry.getRows();
            subRow = entry.getRows() - (rowCount - row);
            if (rowCount > row) {
                break;
            }
        }

        if (entry == null) {
            return null;
        }
        if (subRow > 0 && column != 3) {
            return null;
        }
        if (entry.getDividerType() != IncidentEntry.Divider.NONE) {
            if (column == 5) {
                return entry.getDividerType();
            } else {
                return null;
            }
        }

        IncidentInfo a = entry.getIncident();
        switch (column) {
            case 0:
                return String.valueOf(row);
            case 1:
                return TimeUtils.asDuration(a.getSessionEarliestTime());
            case 2:
                if (a.getReplayTime() != 0) {
                    return TimeUtils.asDuration(a.getReplayTime());
                }
                return "-";
            case 3:
                int lower = MAX_CARS_PER_ROW * subRow;
                int upper = Math.min(MAX_CARS_PER_ROW * (subRow + 1), a.getCars().size());
                return a.getCars().subList(lower, upper);
            case 4:
                return "-";
        }
        return null;
    }

    @Override
    public void onClick(int column, int row, int mouseX, int mouseY) {
        IncidentEntry entry = null;
        int subRow = 0;
        //find correct Entry
        int rowCount = 0;
        for (int i = 0; i < incidents.size(); i++) {
            entry = incidents.get(i);
            rowCount += entry.getRows();
            subRow = entry.getRows() - (rowCount - row);
            if (rowCount > row) {
                break;
            }
        }
        if (entry == null) {
            return;
        }

        if (column == 3) {
            //Car column clicked
            int index = (int) (mouseX / (LINE_HEIGHT * 1.25f)) + MAX_CARS_PER_ROW * subRow;
            if (index >= entry.getIncident().getCars().size()) {
                return;
            }
            extension.focusOnCar(entry.getIncident().getCars().get(index).getCarId());

        } else if (column == 4 && subRow == 0) {
            //replay column clicked on the first sub row.
            int button = (int) Math.floor((mouseX - LINE_HEIGHT) / (LINE_HEIGHT * 1.5f));
            if (button > 2 || button < 0) {
                return;
            }
            int seconds = 20;
            if (button == 1) {
                seconds = 10;
            } else if (button == 2) {
                seconds = 5;
            }
            extension.startAccidentReplay(entry.getIncident(), seconds);
        }
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

            if (currentlyConnectedCars.contains(car.getCarId())) {
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
            if (!currentlyConnectedCars.contains(car.getCarId())) {
                applet.fill(0, 0, 0, 150);
                applet.rect(x + 1, 1, w - 2, context.height - 2);
            }

            x += w;
        }
    };

    private final LPTable.CellRenderer replayButtonRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        int width = (int) (LINE_HEIGHT * 1.5f);
        int x = LINE_HEIGHT;
        for (String t : Arrays.asList("-20s", "-10s", "-5s")) {
            if (context.isMouseOverColumn
                    && context.isMouseOverRow
                    && context.mouseX > x
                    && context.mouseX < x + width) {
                applet.fill(COLOR_RED);
            } else {
                applet.fill(COLOR_GRAY);
            }
            applet.rect(x + 1, 1, width - 2, context.height - 2);
            applet.fill(COLOR_WHITE);
            applet.textAlign(CENTER, CENTER);
            applet.text(t, x + width / 2f, context.height / 2);
            x += width;
        }
    };

    private final LPTable.CellRenderer dividerRender = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        switch ((IncidentEntry.Divider) context.object) {
            case PRACTICE:
                applet.fill(COLOR_PRACTICE);
                break;
            case QUALIFYING:
                applet.fill(COLOR_QUALIFYING);
                break;
            case RACE:
                applet.fill(COLOR_RACE);
                break;
        }
        applet.rect(-context.tablePosX + 1, 1, context.tableWidth - 2, context.height - 2);
        applet.fill(0);
        applet.textAlign(CENTER, CENTER);
        applet.text(((IncidentEntry.Divider) context.object).name(),
                -context.tablePosX + context.tableWidth / 2f,
                context.height / 2f);
    };

    public void setAccidents(List<IncidentEntry> incidents) {
        this.incidents = incidents;
    }

    public void setConnectedCars(List<Integer> connectedCars) {
        this.currentlyConnectedCars = connectedCars;
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
