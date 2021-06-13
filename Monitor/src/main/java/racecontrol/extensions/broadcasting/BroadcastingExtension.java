/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting;

import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.client.events.TrackData;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.eventbus.Event;
import racecontrol.extensions.livetiming.LiveTimingExtension;
import racecontrol.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class BroadcastingExtension
        extends AccClientExtension {

    public static final Logger LOG = Logger.getLogger(BroadcastingExtension.class.getName());
    /**
     * Panel.
     */
    private final BroadcastingPanel panel;
    /**
     * Reference to the live timing extension.
     */
    private final LiveTimingExtension liveTimingExtension;

    public BroadcastingExtension(AccBroadcastingClient client) {
        super(client);

        liveTimingExtension = client.getOrCreateExtension(LiveTimingExtension.class);

        this.panel = new BroadcastingPanel(this, liveTimingExtension.getPanel());
    }

    @Override
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
        getClient().sendSetHudPageRequest(page);
    }

    public void setCameraSet(String camSet, String cam) {
        LOG.info("Setting camera to " + camSet + " " + cam);
        getClient().sendSetCameraRequest(camSet, cam);
    }

    public void startInstantReplay(float seconds, float duration) {
        LOG.info("Starting instant replay for " + seconds + " seconds");
        getClient().sendInstantReplayRequestSimple(seconds, duration);
    }

}
