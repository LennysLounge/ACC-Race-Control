/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.broadcasting;

import racecontrol.gui.app.broadcasting.timing.LiveTimingController;
import java.util.logging.Logger;
import racecontrol.gui.RaceControlApplet;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.TrackDataEvent;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class BroadcastingController
        implements EventListener {

    public static final Logger LOG = Logger.getLogger(BroadcastingController.class.getName());
    /**
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;
    /**
     * Panel.
     */
    private final BroadcastingPanel panel;
    /**
     * Reference to the live timing extension.
     */
    private final LiveTimingController liveTimingExtension;

    public BroadcastingController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        liveTimingExtension = new LiveTimingController();
        this.panel = new BroadcastingPanel(this, liveTimingExtension.getPanel());
    }

    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackDataEvent) {
            TrackInfo info = ((TrackDataEvent) e).getInfo();
            RaceControlApplet.runLater(() -> {
                panel.setCameraSets(info.getCameraSets());
            });
        } else if (e instanceof RealtimeUpdateEvent) {
            SessionInfo info = ((RealtimeUpdateEvent) e).getSessionInfo();
            panel.setActiveCameraSet(info.getActiveCameraSet(), info.getActiveCamera());
            panel.setActiveHudPage(info.getCurrentHudPage());
        }
    }

    public void setHudPage(String page) {
        LOG.info("Setting HUD page to " + page);
        client.sendSetHudPageRequest(page);
    }

    public void setCameraSet(String camSet, String cam) {
        LOG.info("Setting camera to " + camSet + " " + cam);
        client.sendSetCameraRequest(camSet, cam);
    }

    public void startInstantReplay(float seconds, float duration) {
        LOG.info("Starting instant replay for " + seconds + " seconds");
        client.sendInstantReplayRequestSimple(seconds, duration);
    }

}
