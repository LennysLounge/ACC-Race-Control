/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

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
import static racecontrol.client.extension.statistics.CarProperties.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.CATEGORY;
import static racecontrol.client.extension.statistics.CarProperties.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarProperties.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarProperties.NAME;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_FINISHED;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.gui.LookAndFeel.COLOR_BLACK;

/**
 *
 * @author Leonard
 */
public abstract class LiveTimingTableModel
        extends LPTableModel {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(LiveTimingTableModel.class.getName());
    /**
     * Ordered list of car entries
     */
    protected List<CarStatistics> entries = new LinkedList<>();
    /**
     * Column shows the position number.
     */
    protected final LPTableColumn positionColumn = new LPTableColumn("Pos")
            .setMinWidth((int) (LINE_HEIGHT * 1.2f))
            .setMaxWidth((int) (LINE_HEIGHT * 1.2f))
            .setPriority(1000)
            .setCellRenderer((applet, context) -> positionRenderer(applet, context));

    protected final LPTableColumn nameColumn = new LPTableColumn("Name")
            .setMaxWidth(LINE_HEIGHT * 5f)
            .setMinWidth(LINE_HEIGHT * 5f)
            .setPriority(1000)
            .setCellRenderer((applet, context) -> nameRenderer(applet, context));

    protected final LPTableColumn pitColumn = new LPTableColumn("")
            .setMaxWidth((int) (LINE_HEIGHT * 0.4f))
            .setMinWidth((int) (LINE_HEIGHT * 0.4f))
            .setPriority(1000)
            .setCellRenderer((applet, context) -> pitRenderer(applet, context));

    protected final LPTableColumn carNumberColumn = new LPTableColumn("#")
            .setMinWidth(LINE_HEIGHT * 1.5f)
            .setMaxWidth(LINE_HEIGHT * 1.5f)
            .setPriority(1000)
            .setCellRenderer((applet, context) -> carNumberRenderer(applet, context));

    private void positionRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (stats.get(IS_FOCUSED_ON)) {
            bgColor = LookAndFeel.COLOR_WHITE;
            fgColor = LookAndFeel.COLOR_BLACK;
        }
        applet.fill(bgColor);
        applet.rect(1, 1, context.width - 2, context.height - 2);
        applet.fill(fgColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(stats.get(REALTIME_POSITION)),
                context.width / 2f, context.height / 2f);
    }

    private void nameRenderer(PApplet applet, LPTable.RenderContext context) {
        String name = ((CarStatistics) context.object).get(NAME);

        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(1, 1, context.width - 1, context.height - 2);
        }

        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(name, context.height / 4f, context.height / 2f);
    }

    private void pitRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, context.width - 1, context.height - 2);
        }
        if (stats.get(SESSION_FINISHED)) {
            applet.fill(COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            float s = (context.width - 2) / 2;
            applet.fill(COLOR_BLACK);
            int i = 0;
            while ((s * (i + 1) + 2) < context.height) {
                applet.rect(1 + s * (i % 2), 1 + s * i, s, s);
                i++;
            }
            applet.rect(1 + s * (i % 2), 1 + s * i, s, context.height - (2 + s * i));
        } else if (stats.get(IS_IN_PITS)) {
            applet.noStroke();
            applet.fill(COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            applet.fill(0);
            applet.textAlign(CENTER, CENTER);
            applet.textSize(TEXT_SIZE * 0.6f);
            applet.text("P", context.width / 2f, context.height / 2f);
            applet.textFont(LookAndFeel.fontMedium());
            applet.textSize(LookAndFeel.TEXT_SIZE);
        }
    }

    private void carNumberRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        int backColor = 0;
        int frontColor = 0;
        switch (stats.get(CATEGORY)) {
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
        CarType type = getCarType(stats.get(CAR_MODEL));
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
        applet.text(String.valueOf(stats.get(CAR_NUMBER)),
                context.width / 2f, context.height / 2f);
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getSelectedRow() {
        for (int i = 0; i < entries.size(); i++) {
            var stats = entries.get(i);
            if (stats.get(IS_FOCUSED_ON)) {
                return i;
            }
        }
        return -1;
    }

    public abstract void sort();

    public abstract String getName();

    public void setEntries(List<CarStatistics> entries) {
        this.entries = entries;
    }

    public CarStatistics getEntry(int row) {
        if (row < entries.size()) {
            return entries.get(row);
        }
        return null;
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
