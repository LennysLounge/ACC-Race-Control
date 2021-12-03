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
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.gui.lpui.table.LPTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import racecontrol.client.data.enums.CarCategory;
import static racecontrol.client.data.enums.CarCategory.CUP;
import static racecontrol.client.data.enums.CarCategory.GT3;
import static racecontrol.client.data.enums.CarCategory.ST;
import static racecontrol.client.data.enums.SessionType.RACE;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.CATEGORY;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarProperties.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarProperties.IS_IN_PITS;
import static racecontrol.client.extension.statistics.CarProperties.IS_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarProperties.IS_YELLOW_FLAG;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarProperties.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarProperties.LAP_TIME_GAP_TO_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarProperties.NAME;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_FINISHED;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.gui.LookAndFeel.COLOR_BLACK;
import static racecontrol.gui.LookAndFeel.COLOR_ORANGE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.COLOR_YELLOW;
import racecontrol.utility.TimeUtils;

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
            .setCellRenderer(this::positionRenderer);

    protected final LPTableColumn nameColumn = new LPTableColumn("Name")
            .setMaxWidth(LINE_HEIGHT * 5f)
            .setMinWidth(LINE_HEIGHT * 5f)
            .setPriority(1000)
            .setCellRenderer(this::nameRenderer);

    protected final LPTableColumn pitColumn = new LPTableColumn("")
            .setMaxWidth((int) (LINE_HEIGHT * 0.4f))
            .setMinWidth((int) (LINE_HEIGHT * 0.4f))
            .setPriority(1000)
            .setCellRenderer(this::pitRenderer);

    protected final LPTableColumn carNumberColumn = new LPTableColumn("#")
            .setMinWidth(LINE_HEIGHT * 1.5f)
            .setMaxWidth(LINE_HEIGHT * 1.5f)
            .setPriority(1000)
            .setCellRenderer(this::carNumberRenderer);

    protected void positionRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        applet.noStroke();
        int bgColor = LookAndFeel.COLOR_RED;
        int fgColor = LookAndFeel.COLOR_WHITE;
        if (stats.get(IS_SESSION_BEST)) {
            bgColor = LookAndFeel.COLOR_PURPLE;
            fgColor = LookAndFeel.COLOR_WHITE;
        } else if (stats.get(IS_FOCUSED_ON)) {
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

    protected void nameRenderer(PApplet applet, LPTable.RenderContext context) {
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

    protected void pitRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        if (context.isMouseOverRow) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, 1, context.width - 1, context.height - 2);
        }
        if (stats.get(SESSION_FINISHED)) {
            applet.fill(COLOR_WHITE);
            applet.rect(1, 1, context.width - 2, context.height - 2);
            float w = (context.width - 2) / 2;
            float h = (context.height - 2) / 6;
            applet.fill(COLOR_BLACK);
            for (int i = 0; i < 6; i++) {
                applet.rect(1 + w * (i % 2), 1 + h * i, w, h);
            }
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
        } else if (stats.get(IS_YELLOW_FLAG)) {
            applet.fill(COLOR_YELLOW);
            applet.rect(1, 1, context.width - 2, context.height - 2);
        }
    }

    protected void carNumberRenderer(PApplet applet, LPTable.RenderContext context) {
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
        CarCategory cat = stats.get(CAR_MODEL).getCategory();
        if (cat != GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(context.width - 1, context.height - 1);
            applet.vertex(context.width - 1, context.height - LINE_HEIGHT * 0.55f);
            applet.vertex(context.width - LINE_HEIGHT * 0.55f, context.height - 1);
            applet.endShape(CLOSE);
            applet.stroke(0, 0, 0, 50);
            applet.line(context.width - 1, context.height - LINE_HEIGHT * 0.55f,
                    context.width - LINE_HEIGHT * 0.55f, context.height);
            applet.noStroke();
            if (cat == ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (cat == CUP) {
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

    protected void gapRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        applet.fill(COLOR_WHITE);
        String text = "--";

        if (stats.get(SESSION_ID).getType() == RACE) {
            int gap = stats.get(GAP_TO_POSITION_AHEAD);
            if (stats.get(REALTIME_POSITION) > 1) {
                text = TimeUtils.asGap(gap);
            }

            if (gap < 1000 && gap > 0) {
                applet.fill(COLOR_ORANGE);
            }
        } else {
            if (stats.get(BEST_LAP_TIME) != Integer.MAX_VALUE
                    && stats.get(LAP_TIME_GAP_TO_SESSION_BEST) != 0) {
                text = TimeUtils.asDelta(stats.get(LAP_TIME_GAP_TO_SESSION_BEST));
            }
        }

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width - 20, context.height / 2);
    }

    protected void gapToLeaderRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        String text = "--";
        if (stats.get(SESSION_ID).getType() == RACE) {
            if (stats.get(LAPS_BEHIND_SPLIT)) {
                text = String.format("+%d Laps", stats.get(LAPS_BEHIND_LEADER));
            } else {
                if (stats.get(GAP_TO_LEADER) != 0) {
                    text = TimeUtils.asGap(stats.get(GAP_TO_LEADER));
                }
            }
        }
        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(text, context.width - 20, context.height / 2);
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

}
