/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.broadcasting.timing.tablemodels;

import racecontrol.gui.app.broadcasting.timing.LiveTimingEntry;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_GT4;
import static racecontrol.gui.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPTable;
import racecontrol.gui.lpui.LPTableColumn;
import racecontrol.gui.lpui.LPTableModel;
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
        extends LPTableModel {

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
            LPTable.RenderContext context) -> {
        LiveTimingEntry entry = (LiveTimingEntry) context.object;
        
        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (entry.getCarInfo().getCarId() == focusedCarId) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, context.width - 2, context.height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(entry.getCarInfo().getRealtime().getPosition()),
                context.width / 2f, context.height / 2f);
    };
    /**
     * Column shows the position number.
     */
    protected final LPTableColumn positionColumn = new LPTableColumn("Pos")
            .setMinWidth((int)(LINE_HEIGHT * 1.2f))
            .setMaxWidth((int)(LINE_HEIGHT * 1.2f))
            .setPriority(1000)
            .setCellRenderer(positionRenderer);
    
    private final LPTable.CellRenderer nameRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
        String firstname = car.getDriver().getFirstName();
        String lastname = car.getDriver().getLastName();
        firstname = firstname.substring(0, Math.min(firstname.length(), 1));
        String name = String.format("%s. %s", firstname, lastname);
        
        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(1, 1, context.width - 1, context.height - 2);
        }
        
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(name, context.height / 4f, context.height / 2f);
    };
    
    protected final LPTableColumn nameColumn = new LPTableColumn("Name")
            .setMaxWidth(LINE_HEIGHT * 5f)
            .setMinWidth(LINE_HEIGHT * 5f)
            .setGrowthRate(3)
            .setPriority(1000)
            .setCellRenderer(nameRenderer);
    
    private final LPTable.CellRenderer pitRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        boolean isInPits = ((LiveTimingEntry) context.object).getCarInfo()
                .getRealtime().getLocation() != CarLocation.TRACK;
        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, context.width - 1, context.height - 2);
        }
        if (isInPits) {
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(TEXT_SIZE * 0.6f);
            applet.text("P", context.width / 2f, context.height / 2f);
            applet.textFont(LookAndFeel.fontMedium());
            applet.textSize(LookAndFeel.TEXT_SIZE);
        }
    };
    
    protected final LPTableColumn pitColumn = new LPTableColumn("")
            .setMaxWidth((int)(LINE_HEIGHT * 0.4f))
            .setMinWidth((int)(LINE_HEIGHT * 0.4f))
            .setPriority(1000)
            .setCellRenderer(pitRenderer);
    
    private final LPTable.CellRenderer carNumberRenderer = (
            PApplet applet,
            LPTable.RenderContext context) -> {
        CarInfo car = ((LiveTimingEntry) context.object).getCarInfo();
        
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
        applet.rect(1, 1, context.width - 2, context.height - 2);

        //render GT4 / Cup / Super trofeo corners.
        CarType type = getCarType(car.getCarModelType());
        if (type != CarType.GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(context.width - 1, context.height - 1);
            applet.vertex(context.width - 1, context.height - LINE_HEIGHT * 0.5f);
            applet.vertex(context.width - LINE_HEIGHT * 0.5f, context.height - 1);
            applet.endShape(CLOSE);
            if (type == CarType.ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (type == CarType.CUP) {
                applet.fill(LookAndFeel.COLOR_PORSCHE_CUP);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(context.width - 1, context.height - 1);
            applet.vertex(context.width - 1, context.height - LINE_HEIGHT * 0.4f);
            applet.vertex(context.width - LINE_HEIGHT * 0.4f, context.height - 1);
            applet.endShape(CLOSE);
        }
        
        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(car.getCarNumber()), context.width / 2f, context.height / 2f);
    };
    
    protected final LPTableColumn carNumberColumn = new LPTableColumn("#")
            .setMinWidth(LINE_HEIGHT * 1.5f)
            .setMaxWidth(LINE_HEIGHT * 1.5f)
            .setPriority(1000)
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
