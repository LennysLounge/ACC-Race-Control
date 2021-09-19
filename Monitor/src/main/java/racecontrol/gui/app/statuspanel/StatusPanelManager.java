/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.statuspanel;

import racecontrol.client.events.ReplayEndedEvent;
import racecontrol.client.events.ReplayStartedEvent;
import racecontrol.client.extension.replayoffset.ReplayOffsetSearchStartedEvent;
import racecontrol.client.extension.replayoffset.ReplayStartKnownEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.app.AppPanel;
import racecontrol.gui.lpui.LPComponent;

/**
 * Manages the status panels.
 *
 * @author Leonard
 */
public class StatusPanelManager
        implements EventListener {

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
    /**
     * The replay offset search status panel.
     */
    private final ReplayOffsetSearchStatusPanel replayOffsetSearchStatusPanel;
    /**
     * True if the program is currently searching for the replay time.
     */
    private boolean isReplaySearchGoingOn;
    /**
     * The replay playing status panel.
     */
    private final ReplayPlayingStatusPanel replayPlayingStatusPanel;

    public static StatusPanelManager getInstance() {
        if (instance == null) {
            instance = new StatusPanelManager();
        }
        return instance;
    }

    private StatusPanelManager() {
        EventBus.register(this);
        replayOffsetSearchStatusPanel = new ReplayOffsetSearchStatusPanel();
        replayPlayingStatusPanel = new ReplayPlayingStatusPanel();
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

    @Override
    public void onEvent(Event e) {
        if (e instanceof ReplayOffsetSearchStartedEvent) {
            addStatusPanel(replayOffsetSearchStatusPanel);
            isReplaySearchGoingOn = true;
        } else if (e instanceof ReplayStartKnownEvent) {
            removeStatusPanel(replayOffsetSearchStatusPanel);
            isReplaySearchGoingOn = false;
        } else if (e instanceof ReplayStartedEvent) {
            if (!isReplaySearchGoingOn) {
                addStatusPanel(replayPlayingStatusPanel);
            }
        } else if (e instanceof ReplayEndedEvent) {
            removeStatusPanel(replayPlayingStatusPanel);
        }
    }
}
