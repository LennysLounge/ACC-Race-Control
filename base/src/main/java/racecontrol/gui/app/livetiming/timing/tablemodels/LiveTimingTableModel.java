/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import racecontrol.gui.LookAndFeel;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import static racecontrol.client.extension.statistics.CarStatistics.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_LEADER;
import static racecontrol.client.extension.statistics.CarStatistics.GAP_TO_POSITION_AHEAD;
import static racecontrol.client.extension.statistics.CarStatistics.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarStatistics.LAPS_BEHIND_LEADER;
import static racecontrol.client.extension.statistics.CarStatistics.LAPS_BEHIND_SPLIT;
import static racecontrol.client.extension.statistics.CarStatistics.LAP_TIME_GAP_TO_SESSION_BEST;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.gui.LookAndFeel.COLOR_ORANGE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
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

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
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
