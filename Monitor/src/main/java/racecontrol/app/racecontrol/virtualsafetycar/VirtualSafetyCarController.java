/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.virtualsafetycar;

import racecontrol.app.AppController;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class VirtualSafetyCarController
        implements EventListener {

    /**
     * Settings panel.
     */
    private final VirtualSafetyCarPanel panel = new VirtualSafetyCarPanel();
    /**
     * The app controller.
     */
    private final AppController appController;
    /**
     * The status panel to show a VSC is active.
     */
    private final VSCStatusPanel statusPanel;
    /**
     * Indicates that the virtual safety car is on.
     */
    private boolean vscOn = false;
    /**
     * Speed limit for the vsc.
     */
    private int speedLimit = 0;
    /**
     * Speed tolerance for the vsc.
     */
    private int speedTolerance = 0;
    /**
     * Time tolerance for the vsc.
     */
    private int timeTolerance = 0;

    public VirtualSafetyCarController() {
        EventBus.register(this);
        appController = AppController.getInstance();
        statusPanel = new VSCStatusPanel();

        panel.startButton.setAction(() -> startVSC());
        panel.stopButton.setAction(() -> stopVSC());
        statusPanel.settingsButton.setAction(() -> openSettingsPanel());
    }

    @Override
    public void onEvent(Event e) {
        if (vscOn) {
            statusPanel.invalidate();

        }
    }

    public void startVSC() {
        if (!vscOn) {
            vscOn = true;
            EventBus.publish(new VSCStartEvent());
            appController.addStatusPanel(statusPanel);
        }
        // disable start button
        panel.isVSCDisabled = false;
        panel.updateComponents();
        panel.invalidate();
    }

    public void stopVSC() {
        if (vscOn) {
            vscOn = false;
            EventBus.publish(new VSCEndEvent());
            appController.removeStatusPanel(statusPanel);
        }
        // enable start button
        panel.isVSCDisabled = true;
        panel.updateComponents();
        panel.invalidate();
    }

    public void openSettingsPanel() {
        appController.launchNewWindow(panel, false);
    }

}
