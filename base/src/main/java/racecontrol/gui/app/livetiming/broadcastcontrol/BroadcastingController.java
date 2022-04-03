/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.broadcastcontrol;

import java.util.logging.Logger;
import racecontrol.gui.RaceControlApplet;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.protocol.TrackInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.TrackInfoEvent;
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

    public BroadcastingController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        this.panel = new BroadcastingPanel(this);
    }

    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackInfoEvent) {
            RaceControlApplet.runLater(() -> {
                panel.setCameraSets(((TrackInfoEvent) e).getInfo().getCameraSets());
            });
        } else if (e instanceof RealtimeUpdateEvent) {
            RaceControlApplet.runLater(() -> {
                SessionInfo info = ((RealtimeUpdateEvent) e).getSessionInfo();
                panel.setActiveCameraSet(info.getActiveCameraSet(), info.getActiveCamera());
                panel.setActiveHudPage(info.getCurrentHudPage());
            });
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
