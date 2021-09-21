/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming;

import racecontrol.gui.app.AppController;
import racecontrol.gui.app.PanelWindowApplet;
import racecontrol.gui.app.livetiming.broadcastcontrol.BroadcastingController;
import racecontrol.gui.app.livetiming.timing.LiveTimingTableController;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LiveTimingController {

    private final LiveTimingPanel panel;

    private final LiveTimingTableController liveTimingTableController;

    private final BroadcastingController broadcastingController;

    private final AppController appController;

    private boolean isLiveTimingDetached;

    public LiveTimingController() {
        appController = AppController.getInstance();
        liveTimingTableController = new LiveTimingTableController();
        broadcastingController = new BroadcastingController();

        panel = new LiveTimingPanel(broadcastingController.getPanel());
        panel.addLiveTimingTable(liveTimingTableController.getPanel());

        panel.detachLiveTimingButton.setAction(() -> detachLiveTiming());
        panel.viewLeftButton.setAction(() -> cycleTableModelsLeft());
        panel.viewRightButton.setAction(() -> cycleTableModelsRight());

    }

    public LPContainer getPanel() {
        return panel;
    }

    private void detachLiveTiming() {
        if (isLiveTimingDetached) {
            return;
        }
        panel.removeLiveTimingTable(liveTimingTableController.getPanel());
        isLiveTimingDetached = true;
        PanelWindowApplet applet = appController.launchNewWindow(liveTimingTableController.getPanel(), true);
        applet.addCloseAction(() -> {
            isLiveTimingDetached = false;
            panel.addLiveTimingTable(liveTimingTableController.getPanel());
        });
    }

    public void cycleTableModelsLeft() {
        liveTimingTableController.cycleTableModelsLeft();
        panel.viewLabel.setText("View " + liveTimingTableController.getTableModelName());
        panel.onResize(panel.getWidth(), panel.getHeight());
        panel.invalidate();
    }

    public void cycleTableModelsRight() {
        liveTimingTableController.cycleTableModelsRight();
        panel.viewLabel.setText("View " + liveTimingTableController.getTableModelName());
        panel.onResize(panel.getWidth(), panel.getHeight());
        panel.invalidate();
    }

}
