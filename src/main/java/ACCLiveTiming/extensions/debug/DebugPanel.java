/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.debug;

import ACCLiveTiming.networking.data.SessionInfo;
import ACCLiveTiming.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class DebugPanel extends LPContainer {

    private DebugExtension extension;

    public DebugPanel(DebugExtension extension) {
        this.extension = extension;

        setName("DEBUG");
    }

    @Override
    public void draw() {
        SessionInfo session = extension.getModel().getSessionInfo();
        
        applet.fill(255);
        applet.text("Ambient:" + session.getAmbientTemp(), 20, 20);
        applet.text("current hud page:" + session.getCurrentHudPage(), 20, 40);
        applet.text("cloud level:" + session.getCloudLevel(), 20, 60);
        applet.text("focused car:" + session.getFocusedCarIndex(), 20, 80);
        applet.text("session end time:" + session.getSessionEndTime(), 20, 100);
        applet.text("session time remaining:" + session.getSessionTime(), 20, 120);
        applet.text("track temp:" + session.getTrackTemp(), 20, 140);

    }

}
