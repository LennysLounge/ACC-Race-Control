/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.virtualsafetycar;

import racecontrol.client.extension.vsc.VirtualSafetyCarExtension;
import racecontrol.gui.app.AppController;
import racecontrol.client.extension.vsc.events.VSCEndEvent;
import racecontrol.client.extension.vsc.events.VSCStartEvent;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.statuspanel.StatusPanelManager;

/**
 *
 * @author Leonard
 */
public class VirtualSafetyCarConfigController
        implements EventListener {

    /**
     * Settings panel.
     */
    private final VirtualSafetyCarConfigPanel panel = new VirtualSafetyCarConfigPanel();
    /**
     * The status panel manager.
     */
    private final StatusPanelManager statusPanelManager;
    /**
     * App controller.
     */
    private final AppController appController;
    /**
     * Reference to the vsc controller.
     */
    private final VirtualSafetyCarExtension vscController;
    /**
     * The status panel to show a VSC is active.
     */
    private final VSCStatusPanel statusPanel;

    public VirtualSafetyCarConfigController() {
        EventBus.register(this);
        statusPanelManager = StatusPanelManager.getInstance();
        appController = AppController.getInstance();
        vscController = VirtualSafetyCarExtension.getInstance();
        statusPanel = new VSCStatusPanel();

        panel.startButton.setAction(() -> startVSC());
        panel.stopButton.setAction(() -> vscController.stopVSC());
        statusPanel.settingsButton.setAction(() -> openSettingsPanel());
    }

    public void openSettingsPanel() {
        appController.launchNewWindow(panel, false);
    }

    private void startVSC() {
        vscController.startVSC(panel.targetSpeedTextField.getNumber(),
                panel.speedToleranceTextField.getNumber(),
                panel.timeToleranceTextField.getNumber() * 1000);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof VSCStartEvent) {
            RaceControlApplet.runLater(() -> {
                panel.isVSCDisabled = false;
                panel.updateComponents();
                panel.invalidate();

                statusPanel.setVSCStart();
                statusPanelManager.addStatusPanel(statusPanel);
            });
        } else if (e instanceof VSCEndEvent) {
            RaceControlApplet.runLater(() -> {
                panel.isVSCDisabled = true;
                panel.updateComponents();
                panel.invalidate();

                statusPanelManager.removeStatusPanel(statusPanel);
            });
        } else if (e instanceof AfterPacketReceivedEvent) {
            RaceControlApplet.runLater(() -> {
                if (vscController.isActive()) {
                    statusPanel.invalidate();
                }
            });
        }
    }

}
