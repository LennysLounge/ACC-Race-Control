/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.broadcasting;

import racecontrol.app.broadcasting.timing.LiveTimingController;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.client.events.TrackData;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.lpgui.gui.LPContainer;

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
        if (e instanceof TrackData) {
            TrackInfo info = ((TrackData) e).getInfo();
            panel.setCameraSets(info.getCameraSets());
        } else if (e instanceof RealtimeUpdate) {
            SessionInfo info = ((RealtimeUpdate) e).getSessionInfo();
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
