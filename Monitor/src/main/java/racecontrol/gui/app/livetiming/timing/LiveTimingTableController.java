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
import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.SessionType.PRACTICE;
import static racecontrol.client.data.enums.SessionType.QUALIFYING;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.SessionChangedEvent;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.gui.app.livetiming.timing.tablemodels.QualifyingTableModel;
import racecontrol.gui.app.livetiming.timing.tablemodels.RaceTableModel;
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

    private final QualifyingTableModel qualifyingTableModel;

    private final RaceTableModel raceTableModel;
    /**
     * Weather ot not to automatically switch the table model when the session
     * changes.
     */
    private boolean autoSwitchTableModel = true;

    public LiveTimingTableController() {
        EventBus.register(this);
        statisticsExtension = StatisticsExtension.getInstance();
        client = AccBroadcastingClient.getClient();
        table.setCellClickAction((column, row) -> onCellClickAction(column, row));

        qualifyingTableModel = new QualifyingTableModel();
        raceTableModel = new RaceTableModel();
        tableModels.add(qualifyingTableModel);
        tableModels.add(raceTableModel);
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
        } else if (e instanceof SessionChangedEvent) {
            sessionChanged(((SessionChangedEvent) e).getSessionInfo());
        }
    }

    private void updateTableModel() {
        List<CarStatistics> cars = new ArrayList<>();
        client.getModel().getCarsInfo().values().forEach(
                car -> cars.add(statisticsExtension.getCar(car.getCarId()))
        );

        model.setEntries(cars);
        model.sort();
        table.invalidate();
    }

    private void sessionChanged(SessionInfo info) {
        if (autoSwitchTableModel) {
            if (info.getSessionType() == RACE) {
                table.setTableModel(raceTableModel);
                model = raceTableModel;
            } else if (info.getSessionType() == QUALIFYING
                    || info.getSessionType() == PRACTICE) {
                table.setTableModel(qualifyingTableModel);
                model = qualifyingTableModel;
            }
        }
        autoSwitchTableModel = true;
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
        model = tableModels.get((index + 1) % tableModels.size());
        table.setTableModel(model);
        autoSwitchTableModel = false;
    }

    public void cycleTableModelsRight() {
        int index = tableModels.indexOf(model);
        if (index == 0) {
            index = tableModels.size();
        }
        model = tableModels.get(index - 1);
        table.setTableModel(model);
        autoSwitchTableModel = false;
    }

    public String getTableModelName() {
        return model.getName();
    }

}
