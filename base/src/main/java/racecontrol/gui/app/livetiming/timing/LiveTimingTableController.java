/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing;

import java.util.ArrayList;
import java.util.List;
import racecontrol.gui.app.livetiming.timing.tablemodels.LiveTimingTableModel;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.gui.lpui.LPContainer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static racecontrol.client.extension.statistics.CarStatistics.CAR_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.livetiming.timing.tablemodels.DriversTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.QualifyingBestTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.QualifyingLastTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.RaceTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.RelativeTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.StatsTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.TestTableModel;
import racecontrol.gui.lpui.table.LPTable;

/**
 *
 * @author Leonard
 */
public class LiveTimingTableController
        implements EventListener {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(LiveTimingTableController.class.getName());
    /**
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;
    /**
     * The statistics extension.
     */
    private final StatisticsExtension statisticsExtension;
    /**
     * The live timing table.
     */
    private final LPTable table = new LPTable();
    /**
     * List of available tables models.
     */
    private final List<LiveTimingTableModel> tableModels = new ArrayList<>();
    /**
     * Table models.
     */
    private final LiveTimingTableModel TABLE_MODEL_QUALI_BEST = new QualifyingBestTableModel();
    private final LiveTimingTableModel TABLE_MODEL_QUALI_LAST = new QualifyingLastTableModel();
    private final LiveTimingTableModel TABLE_MODEL_RACE = new RaceTableModel();
    private final LiveTimingTableModel TABLE_MODEL_STATS = new StatsTableModel();
    private final LiveTimingTableModel TABLE_MODEL_DRIVERS = new DriversTableModel();
    private final LiveTimingTableModel TABLE_MODEL_RELATIVE = new RelativeTableModel();
    private final LiveTimingTableModel TABLE_MODEL_TEST = new TestTableModel();
    /**
     * Table model to display the live timing.
     */
    private LiveTimingTableModel model;
    /**
     * timestamp for the last time the table was clicked.
     */
    private long lastTableClick = 0;
    /**
     * Last row that was clicked.
     */
    private int lastTableClickRow = -1;

    public LiveTimingTableController() {
        EventBus.register(this);
        statisticsExtension = StatisticsExtension.getInstance();
        client = AccBroadcastingClient.getClient();
        table.setCellClickAction((column, row) -> onCellClickAction(column, row));

        useRelative(false);

        table.setTableModel(model);
        table.setName("Live Timing Table");
    }

    public LPContainer getPanel() {
        return table;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            RaceControlApplet.runLater(() -> {
                updateTableModel();
            });
        }
    }

    private void updateTableModel() {
        List<CarStatistics> cars = client.getModel().cars.values().stream()
                .map(car -> statisticsExtension.getCar(car.id))
                .collect(Collectors.toList());

        model.setEntries(cars);
        model.sort();
        table.invalidate();
    }

    private void onCellClickAction(int column, int row) {
        if (row >= model.getRowCount()) {
            return;
        }
        //We want to change the focused car when we double click
        if (row == lastTableClickRow) {
            long now = System.currentTimeMillis();
            if (now - lastTableClick < 500) {
                //was double click
                client.sendChangeFocusRequest(
                        ((CarStatistics) model.getEntry(row)).get(CAR_ID));
            }
        }
        lastTableClickRow = row;
        lastTableClick = System.currentTimeMillis();
    }

    public void cycleTableModelsLeft() {
        int index = tableModels.indexOf(model);
        if (index == 0) {
            index = tableModels.size();
        }
        model = tableModels.get(index - 1);
        table.setTableModel(model);
        updateTableModel();
    }

    public void cycleTableModelsRight() {
        int index = tableModels.indexOf(model);
        model = tableModels.get((index + 1) % tableModels.size());
        table.setTableModel(model);
        updateTableModel();
    }

    public void setViewQuali() {
        table.setTableModel(TABLE_MODEL_QUALI_BEST);
        model = TABLE_MODEL_QUALI_BEST;
        updateTableModel();
    }

    public void setViewRace() {
        table.setTableModel(TABLE_MODEL_RACE);
        model = TABLE_MODEL_RACE;
        updateTableModel();
    }

    public String getTableModelName() {
        return model.getName();
    }

    public void useRelative(boolean state) {
        if (state) {
            tableModels.clear();
            tableModels.add(TABLE_MODEL_RELATIVE);
            model = TABLE_MODEL_RELATIVE;
        } else {
            tableModels.clear();
            tableModels.add(TABLE_MODEL_QUALI_BEST);
            tableModels.add(TABLE_MODEL_QUALI_LAST);
            tableModels.add(TABLE_MODEL_RACE);
            tableModels.add(TABLE_MODEL_STATS);
            tableModels.add(TABLE_MODEL_DRIVERS);
            tableModels.add(new TestTableModel());
            model = TABLE_MODEL_RACE;
        }
        table.setTableModel(model);
        updateTableModel();
    }

}
