/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import racecontrol.gui.lpui.table.LPTableColumn;
import static processing.core.PConstants.RIGHT;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsExtension;
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
                .sorted((car1, car2) -> {
                    CarStatistics c1 = StatisticsExtension.getInstance().getCar(car1.id);
                    CarStatistics c2 = StatisticsExtension.getInstance().getCar(car2.id);
                    return c1.get(REALTIME_POSITION).compareTo(c2.get(REALTIME_POSITION));
                })
                .collect(toList());
    }
}
