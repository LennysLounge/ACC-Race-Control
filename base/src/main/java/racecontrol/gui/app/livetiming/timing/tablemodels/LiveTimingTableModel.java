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
import static racecontrol.client.extension.statistics.CarStatistics.IS_FOCUSED_ON;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.model.Car;
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
    protected List<Car> entries = new LinkedList<>();

    protected void gapRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);

        applet.fill(COLOR_WHITE);
        String text = "--";

        if (stats.get(SESSION_ID).getType() == RACE) {
            int gap = car.gapPositionAhead;
            if (stats.get(REALTIME_POSITION) > 1) {
                text = TimeUtils.asGap(gap);
            }

            if (gap < 1000 && gap > 0) {
                applet.fill(COLOR_ORANGE);
            }
        } else {
            if (car.bestLap.getLapTimeMS() != Integer.MAX_VALUE
                    && car.deltaToSessionBest != 0) {
                text = TimeUtils.asDelta(car.deltaToSessionBest);
            }
        }

        applet.textAlign(RIGHT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width - 20, context.height / 2);
    }

    protected void gapToLeaderRenderer(PApplet applet, LPTable.RenderContext context) {
        Car car = (Car) context.object;
        CarStatistics stats = StatisticsExtension.getInstance().getCar(car.id);
        String text = "--";
        if (stats.get(SESSION_ID).getType() == RACE) {
            if (car.gapToLeader != 0) {
                text = TimeUtils.asGap(car.gapToLeader);
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
            Car car = entries.get(i);
            var stats = StatisticsExtension.getInstance().getCar(car.id);
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

    public void setEntries(List<Car> entries) {
        this.entries = entries;
    }

    public Car getEntry(int row) {
        if (row < entries.size()) {
            return entries.get(row);
        }
        return null;
    }

}
