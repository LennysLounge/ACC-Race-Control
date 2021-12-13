/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.autobroadcast;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.autobroadcast.AutobroadcastExtension;
import racecontrol.client.extension.autobroadcast.Entry;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class AutobroadcastController
        implements EventListener {

    private final AccBroadcastingClient client;
    /**
     * Reference to the extension.
     */
    private final AutobroadcastExtension extension;
    /**
     * The panel for this controller.
     */
    private final AutobroadcastPanel panel;
    /**
     * The table model for the rating table.
     */
    private final RatingTableModel tableModel;

    public AutobroadcastController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        extension = AutobroadcastExtension.getInstance();

        panel = new AutobroadcastPanel();
        tableModel = new RatingTableModel();

        panel.ratingTable.setTableModel(tableModel);
        panel.ratingTable.setCellClickAction(this::onCellClickAction);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            RaceControlApplet.runLater(() -> {
                tableModel.setEntriesNew(extension.getEntries());
                if (panel.sortByRatingCheckBox.isSelected()) {
                    tableModel.sortRating();
                } else {
                    tableModel.sortPosition();
                }
                panel.ratingTable.invalidate();
            });
        }
    }

    public LPContainer getPanel() {
        return panel;
    }

    private void onCellClickAction(int column, int row) {
        if (row >= tableModel.getRowCount()) {
            return;
        }
        client.sendChangeFocusRequest(
                ((Entry) tableModel.getEntryNew(row)).getCarInfo().getCarId());
    }

}
