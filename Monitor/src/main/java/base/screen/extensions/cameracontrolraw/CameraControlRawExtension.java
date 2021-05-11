/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.cameracontrolraw;

import base.screen.Main;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.networking.RealtimeUpdate;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.data.TrackInfo;
import base.screen.networking.events.TrackData;
import base.screen.visualisation.gui.LPContainer;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class CameraControlRawExtension
        implements AccClientExtension, EventListener {

    private final static Logger LOG = Logger.getLogger(CameraControlRawExtension.class.getName());

    private final CameraControlRawPanel panel;

    public CameraControlRawExtension() {
        panel = new CameraControlRawPanel(this);
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void removeExtension() {
        EventBus.unregister(this);
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
        Main.getClient().sendSetCameraRequest(camSet, cam);
    }

    public void setHudPage(String page) {
        LOG.info("Setting HUD page to " + page);
        Main.getClient().sendSetHudPageRequest(page);
    }
    
    public void startInstantReplay(int seconds){
        LOG.info("Starting instant replay for " + seconds + " seconds");
        Main.getClient().sendInstantReplayRequest(seconds);
    }

}
