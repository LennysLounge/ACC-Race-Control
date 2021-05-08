/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.livetiming.tablemodels;

import base.screen.extensions.livetiming.LiveTimingEntry;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.LapInfo;
import base.screen.networking.enums.CarLocation;
import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_RED;
import static base.screen.visualisation.LookAndFeel.COLOR_GT4;
import static base.screen.visualisation.LookAndFeel.COLOR_SUPER_TROFEO;
import static base.screen.visualisation.LookAndFeel.COLOR_WHITE;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import base.screen.visualisation.gui.LPTable;
import base.screen.visualisation.gui.LPTableColumn;
import base.screen.visualisation.gui.TableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LiveTimingTableModel
        extends TableModel {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(LiveTimingTableModel.class.getName());

    /**
     * Ordered list of car entries
     */
    private List<LiveTimingEntry> entries = new LinkedList<>();
    /**
     * Car id of the focused car.
     */
    private int focusedCarId = -1;
    /**
     * The best lap of the session.
     */
    private LapInfo sessionBestLap;

    private List<Integer> sessionBestSectors = new LinkedList<>();

    private final LPTable.CellRenderer positionRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        LiveTimingEntry entry = (LiveTimingEntry) object;

        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (entry.getCarInfo().getCarId() == focusedCarId) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, width - 2, height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(entry.getCarInfo().getRealtime().getPosition()),
                width / 2f, height / 2f);
    };
    /**
     * Column shows the position number.
     */
    protected final LPTableColumn positionColumn = new LPTableColumn("P")
            .setMinWidth(50)
            .setMaxWidth(50)
            .setCellRenderer(positionRenderer);

    private final LPTable.CellRenderer nameRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        String firstname = car.getDriver().getFirstName();
        String lastname = car.getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        String name = String.format("%s. %s", firstname, lastname);

        if (isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(1, 1, width - 1, height - 2);
        }

        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(name, height / 4f, height / 2f);
    };

    protected final LPTableColumn nameColumn = new LPTableColumn("Name")
            .setMaxWidth(240)
            .setGrowthRate(3)
            .setCellRenderer(nameRenderer);

    private final LPTable.CellRenderer pitRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        boolean isInPits = ((LiveTimingEntry) object).getCarInfo()
                .getRealtime().getLocation() != CarLocation.TRACK;
        if (isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, width - 1, height - 2);
        }
        if (isInPits) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_WHITE);
            applet.rect(1, 1, width - 2, height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(12);
            applet.text("P", width / 2f, height / 2f);
            applet.textFont(LookAndFeel.fontMedium());
            applet.textSize(LookAndFeel.TEXT_SIZE);
        }
    };

    protected final LPTableColumn pitColumn = new LPTableColumn("")
            .setMaxWidth(16)
            .setMinWidth(16)
            .setCellRenderer(pitRenderer);

    private final LPTable.CellRenderer carNumberRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();

        int backColor = 0;
        int frontColor = 0;
        switch (car.getDriver().getCategory()) {
            case BRONZE:
                backColor = LookAndFeel.COLOR_RED;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
            case SILVER:
                backColor = LookAndFeel.COLOR_GRAY;
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
        applet.rect(1, 1, width - 2, height - 2);

        //render GT4 / Cup / Super trofeo corners.
        CarType type = getCarType(car.getCarModelType());
        if (type != CarType.GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(width - 1, height - 1);
            applet.vertex(width - 1, height - LINE_HEIGHT * 0.5f);
            applet.vertex(width - LINE_HEIGHT * 0.5f, height - 1);
            applet.endShape(CLOSE);
            if (type == CarType.ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (type == CarType.CUP) {
                applet.fill(LookAndFeel.COLOR_PORSCHE_CUP);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(width - 1, height - 1);
            applet.vertex(width - 1, height - LINE_HEIGHT * 0.4f);
            applet.vertex(width - LINE_HEIGHT * 0.4f, height - 1);
            applet.endShape(CLOSE);
        }

        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(car.getCarNumber()), width / 2f, height / 2f);
    };

    protected final LPTableColumn carNumberColumn = new LPTableColumn("#")
            .setMinWidth(60)
            .setMaxWidth(60)
            .setCellRenderer(carNumberRenderer);

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
    }

    public void setEntries(List<LiveTimingEntry> entries) {
        this.entries = entries;
    }

    public LiveTimingEntry getEntry(int row) {
        if (row < entries.size()) {
            return entries.get(row);
        }
        return null;
    }

    public void setFocusedCarId(int carId) {
        focusedCarId = carId;
    }

    public LapInfo getSessionBestLap() {
        return sessionBestLap;
    }

    public void setSessionBestLap(LapInfo sessionBestLap) {
        this.sessionBestLap = sessionBestLap;
    }

    public List<Integer> getSessionBestSectors() {
        return sessionBestSectors;
    }

    public void setSessionBestSectors(List<Integer> sessionBestSectors) {
        this.sessionBestSectors = sessionBestSectors;
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
