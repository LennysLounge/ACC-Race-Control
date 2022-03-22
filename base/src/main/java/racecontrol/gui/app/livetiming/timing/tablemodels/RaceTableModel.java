/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import racecontrol.gui.lpui.table.LPTableColumn;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.extension.statistics.CarProperties.BEST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.LAST_LAP_TIME;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_BEST_LAP_TIME;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_PURPLE;
import static racecontrol.gui.LookAndFeel.COLOR_RACE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTable.RenderContext;
import racecontrol.utility.TimeUtils;
import static racecontrol.client.extension.statistics.CarProperties.CURRENT_LAP_INVALID;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.BestLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CarNumberColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.ConstructorColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.CurrentLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LapCount;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.LastLaptime;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.NameColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.OvertakeIndicator;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PitFlagColumn;
import racecontrol.gui.app.livetiming.timing.tablemodels.columns.PositionColumn;

/**
 *
 * @author Leonard
 */
public class RaceTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            new PositionColumn(),
            new NameColumn(),
            new ConstructorColumn(),
            new CarNumberColumn(),
            new PitFlagColumn(),
            new OvertakeIndicator(),
            new LPTableColumn("Gap")
            .setMinWidth(100)
            .setMaxWidth(100)
            .setTextAlign(RIGHT)
            .setCellRenderer((applet, context) -> gapRenderer(applet, context)),
            new LPTableColumn("To Leader")
            .setMinWidth(100)
            .setMaxWidth(150)
            .setTextAlign(RIGHT)
            .setCellRenderer((applet, context) -> gapToLeaderRenderer(applet, context)),
            new CurrentLaptime(),
            new LastLaptime(),
            new BestLaptime(),
            new LapCount()
        };
    }

    @Override
    public String getName() {
        return "Race";
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(REALTIME_POSITION).compareTo(c2.get(REALTIME_POSITION))
                )
                .collect(toList());
    }

    private void lapTimeRenderer(PApplet applet, RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (stats.get(CAR_LOCATION) == TRACK) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (stats.get(CURRENT_LAP_INVALID)) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(stats.get(CURRENT_LAP_TIME));
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

    private void lastLapRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int lastLapTime = stats.get(LAST_LAP_TIME);
        int bestLapTime = stats.get(BEST_LAP_TIME);
        int sessionbestLapTime = stats.get(SESSION_BEST_LAP_TIME);

        if (lastLapTime == sessionbestLapTime) {
            applet.fill(COLOR_PURPLE);
        } else if (lastLapTime <= bestLapTime) {
            applet.fill(COLOR_RACE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        String text = "--";
        if (lastLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(lastLapTime);
        }
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, context.width / 2, context.height / 2);
    }

}
