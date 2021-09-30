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
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.gui.app.livetiming.timing.tablemodels.QualifyingBestTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.QualifyingLastTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.RaceTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.StatsTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.TestTableModel;
import racecontrol.gui.lpui.LPTable;

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

    private final List<LiveTimingTableModel> tableModels = new ArrayList<>();

    private final QualifyingBestTableModel qualifyingTableModel;

    private final RaceTableModel raceTableModel;

    public LiveTimingTableController() {
        EventBus.register(this);
        statisticsExtension = StatisticsExtension.getInstance();
        client = AccBroadcastingClient.getClient();
        table.setCellClickAction((column, row) -> onCellClickAction(column, row));

        qualifyingTableModel = new QualifyingBestTableModel();
        raceTableModel = new RaceTableModel();

        tableModels.add(qualifyingTableModel);
        tableModels.add(new QualifyingLastTableModel());
        tableModels.add(raceTableModel);
        tableModels.add(new StatsTableModel());
        tableModels.add(new TestTableModel());

        model = tableModels.get(0);
        table.setTableModel(model);
        table.setName("Live Timing Table");
    }

    public LPContainer getPanel() {
        return table;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            updateTableModel();
        }
    }

    private void updateTableModel() {
        List<CarStatistics> cars = client.getModel().getCarsInfo().values().stream()
                .filter(car -> !car.getRealtime().isDefault())
                .map(car -> statisticsExtension.getCar(car.getCarId()))
                .collect(Collectors.toList());

        model.setEntries(cars);
        model.sort();
        table.invalidate();
    }

    private void onCellClickAction(int column, int row) {
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
    }

    public void cycleTableModelsRight() {
        int index = tableModels.indexOf(model);
        model = tableModels.get((index + 1) % tableModels.size());
        table.setTableModel(model);
    }

    public void setViewQuali() {
        table.setTableModel(qualifyingTableModel);
    }

    public void setViewRace() {
        table.setTableModel(raceTableModel);
    }

    public String getTableModelName() {
        return model.getName();
    }

}
