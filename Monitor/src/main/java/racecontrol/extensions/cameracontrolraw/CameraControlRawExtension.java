/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.cameracontrolraw;

import racecontrol.eventbus.Event;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.events.TrackData;
import racecontrol.lpgui.gui.LPContainer;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;

/**
 *
 * @author Leonard
 */
public class CameraControlRawExtension
        extends AccClientExtension {

    private final static Logger LOG = Logger.getLogger(CameraControlRawExtension.class.getName());

    private final CameraControlRawPanel panel;

    public CameraControlRawExtension(AccBroadcastingClient client) {
        super(client);
        panel = new CameraControlRawPanel(this);
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
            panel.setHUDPages(info.getHudPages());
        } else if (e instanceof RealtimeUpdate) {
            SessionInfo info = ((RealtimeUpdate) e).getSessionInfo();
            panel.setActiveCameraSet(info.getActiveCameraSet(), info.getActiveCamera());
            panel.setActiveHudPage(info.getCurrentHudPage());
        }
    }

    public void setCameraSet(String camSet, String cam) {
        LOG.info("Setting camera to " + camSet + " " + cam);
        getClient().sendSetCameraRequest(camSet, cam);
    }

    public void setHudPage(String page) {
        LOG.info("Setting HUD page to " + page);
        getClient().sendSetHudPageRequest(page);
    }

    public void startInstantReplay(float seconds, float duration) {
        LOG.info("Starting instant replay for " + seconds + " seconds");
        getClient().sendInstantReplayRequestSimple(seconds, duration);
    }

}
