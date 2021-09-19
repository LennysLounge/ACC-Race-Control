/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.gui.lpui.LPComponent;

/**
 * Manages the status panels.
 *
 * @author Leonard
 */
public class StatusPanelManager {

    /**
     * Singelton instance.
     */
    private static StatusPanelManager instance;
    /**
     * Panel to add the status panels to.
     */
    private AppPanel panel;
    /**
     * Indicates that the instance is initialised.
     */
    private boolean initialised;

    public static StatusPanelManager getInstance() {
        if (instance == null) {
            instance = new StatusPanelManager();
        }
        return instance;
    }

    public void initialise(AppPanel panel) {
        this.panel = panel;
        initialised = true;
    }

    public void addStatusPanel(LPComponent statusPanel) {
        if (!initialised) {
            return;
        }
        panel.addStatusPanel(statusPanel);
    }

    public void removeStatusPanel(LPComponent statusPanel) {
        if (!initialised) {
            return;
        }
        panel.removeStatusPanel(statusPanel);
    }
}
