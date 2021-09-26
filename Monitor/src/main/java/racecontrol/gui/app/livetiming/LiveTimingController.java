/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming;

import racecontrol.client.data.SessionInfo;
import static racecontrol.client.data.enums.SessionType.PRACTICE;
import static racecontrol.client.data.enums.SessionType.QUALIFYING;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.app.AppController;
import racecontrol.gui.app.PanelWindowApplet;
import racecontrol.gui.app.livetiming.broadcastcontrol.BroadcastingController;
import racecontrol.gui.app.livetiming.timing.LiveTimingTableController;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LiveTimingController
        implements EventListener {

    private final LiveTimingPanel panel;

    private final LiveTimingTableController liveTimingTableController;

    private final BroadcastingController broadcastingController;

    private final AppController appController;

    private boolean isLiveTimingDetached;
    /**
     * Weather ot not to automatically switch the table model when the session
     * changes.
     */
    private boolean autoSwitchTableModel = true;

    public LiveTimingController() {
        appController = AppController.getInstance();
        liveTimingTableController = new LiveTimingTableController();
        broadcastingController = new BroadcastingController();

        panel = new LiveTimingPanel(broadcastingController.getPanel());
        panel.addLiveTimingTable(liveTimingTableController.getPanel());

        panel.detachLiveTimingButton.setAction(() -> detachLiveTiming());
        panel.viewLeftButton.setAction(() -> cycleTableModelsLeft());
        panel.viewRightButton.setAction(() -> cycleTableModelsRight());

        updateViewLabel();
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
        autoSwitchTableModel = false;
        updateViewLabel();
    }

    public void cycleTableModelsRight() {
        liveTimingTableController.cycleTableModelsRight();
        autoSwitchTableModel = false;
        updateViewLabel();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChangedEvent) {
            sessionChanged(((SessionChangedEvent) e).getSessionInfo());
        }
    }

    private void sessionChanged(SessionInfo info) {
        if (autoSwitchTableModel) {
            if (info.getSessionType() == RACE) {
                liveTimingTableController.setViewRace();
                updateViewLabel();
            } else if (info.getSessionType() == QUALIFYING
                    || info.getSessionType() == PRACTICE) {
                liveTimingTableController.setViewQuali();
                updateViewLabel();
            }
        }
        autoSwitchTableModel = true;
    }

    private void updateViewLabel() {
        panel.viewLabel.setText("View " + liveTimingTableController.getTableModelName());
        panel.onResize(panel.getWidth(), panel.getHeight());
        panel.invalidate();
    }

}
