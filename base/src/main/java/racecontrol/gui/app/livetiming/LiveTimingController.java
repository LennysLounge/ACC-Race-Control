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
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import static racecontrol.gui.RaceControlApplet.getApplet;
import racecontrol.gui.app.AppController;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.gui.app.PanelWindowApplet;
import racecontrol.gui.app.livetiming.broadcastcontrol.BroadcastingController;
import racecontrol.gui.app.livetiming.timing.LiveTimingTableController;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class LiveTimingController
        implements EventListener, PageController {

    private final LiveTimingPanel panel;

    private final LiveTimingTableController liveTimingTableController;

    private final BroadcastingController broadcastingController;

    private boolean isLiveTimingDetached;
    /**
     * Weather ot not to automatically switch the table model when the session
     * changes.
     */
    private boolean autoSwitchTableModel = true;

    private final MenuItem menuItem;

    public LiveTimingController() {
        EventBus.register(this);
        menuItem = new MenuItem("Live Timing",
                getApplet().loadResourceAsPImage("/images/RC_Menu_LiveTiming.png"));
        liveTimingTableController = new LiveTimingTableController();
        broadcastingController = new BroadcastingController();

        panel = new LiveTimingPanel(broadcastingController.getPanel());
        panel.addLiveTimingTable(liveTimingTableController.getPanel());

        panel.detachLiveTimingButton.setAction(this::detachLiveTiming);
        panel.viewLeftButton.setAction(this::cycleTableModelsLeft);
        panel.viewRightButton.setAction(this::cycleTableModelsRight);
        panel.relativeCheckBox.setChangeAction(this::toggleUseRelatives);

        updateViewLabel();
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public MenuItem getMenuItem() {
        return menuItem;
    }

    private void detachLiveTiming() {
        if (isLiveTimingDetached) {
            return;
        }
        panel.removeLiveTimingTable(liveTimingTableController.getPanel());
        isLiveTimingDetached = true;
        PanelWindowApplet applet = RaceControlApplet
                .launchNewWindow(liveTimingTableController.getPanel(), true);
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
            RaceControlApplet.runLater(() -> {
                sessionChanged(((SessionChangedEvent) e).getSessionInfo());
            });
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

    private void toggleUseRelatives(boolean state) {
        liveTimingTableController.useRelative(state);
        updateViewLabel();
    }

}
