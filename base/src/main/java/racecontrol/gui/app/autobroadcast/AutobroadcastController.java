/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.autobroadcast;

import java.util.stream.Collectors;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.autobroadcast.AutobroadcastExtension;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import static racecontrol.gui.RaceControlApplet.getApplet;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.PageController;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class AutobroadcastController
        implements EventListener, PageController {

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
    private final CarRatingTableModel carRatingModel;
    /**
     * The table model for the rating table.
     */
    private final CameraRatingTableModel cameraRatingModel;
    /**
     * Menu item.
     */
    private final Menu.MenuItem menuItem;

    public AutobroadcastController() {
        EventBus.register(this);

        menuItem = new Menu.MenuItem("Auto Cam",
                getApplet().loadResourceAsPImage("/images/RC_Menu_AutoBroadcast.png"));

        client = AccBroadcastingClient.getClient();
        extension = AutobroadcastExtension.getInstance();

        panel = new AutobroadcastPanel();
        carRatingModel = new CarRatingTableModel();
        cameraRatingModel = new CameraRatingTableModel();

        panel.ratingTable.setTableModel(carRatingModel);
        panel.ratingTable.setCellClickAction(this::onCellClickAction);

        panel.enableCheckBox.setChangeAction(this::enableCheckboxChanged);
        panel.showCameraRatingsCheckBox.setChangeAction(this::showCameraRatingsChanged);
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            RaceControlApplet.runLater(() -> {
                carRatingModel.setEntriesNew(extension.getCarEntries());
                cameraRatingModel.setEntriesNew(extension.getCameraRatings());
                if (panel.sortByRatingCheckBox.isSelected()) {
                    carRatingModel.sortRating();
                    cameraRatingModel.sortRating();
                } else {
                    carRatingModel.sortPosition();
                    cameraRatingModel.sortName();
                }
                panel.ratingTable.invalidate();

                SessionInfo info = ((RealtimeUpdateEvent) e).getSessionInfo();
                panel.currentCamera.setTextFixed(info.getActiveCameraSet() + " " + info.getActiveCamera());

                int totalScreenTime = extension.getCameraRatings().stream()
                        .map(rating -> rating.screenTime)
                        .reduce(0, Integer::sum);
                String percents = extension.getCameraRatings().stream()
                        .map(rating -> {
                            float p = rating.screenTime * 100f / totalScreenTime;
                            return String.format("%s: %.1f%%", rating.camSet, p);
                        })
                        .collect(Collectors.joining(", "));
                panel.cameraScreenTime.setTextFixed("screen time share: " + percents);
            });
        }
    }

    private void onCellClickAction(int column, int row) {
        if (!panel.showCameraRatingsCheckBox.isSelected()) {
            if (row >= carRatingModel.getRowCount()) {
                return;
            }
            client.sendChangeFocusRequest(
                    carRatingModel.getEntryNew(row).car.id);
        }
    }

    private void enableCheckboxChanged(boolean state) {
        AutobroadcastExtension.getInstance().setEnabled(state);
    }

    private void showCameraRatingsChanged(boolean state) {
        if (state) {
            panel.ratingTable.setTableModel(cameraRatingModel);
        } else {
            panel.ratingTable.setTableModel(carRatingModel);
        }
    }

}
