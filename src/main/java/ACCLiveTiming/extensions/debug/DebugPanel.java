/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.debug;

import ACCLiveTiming.client.ExtensionPanel;
import ACCLiveTiming.networking.data.SessionInfo;
import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public class DebugPanel extends ExtensionPanel {

    private DebugExtension extension;

    public DebugPanel(DebugExtension extension) {
        this.extension = extension;

        this.displayName = "DEBUG";
    }

    @Override
    public void drawPanel(PGraphics base) {
        SessionInfo session = extension.getModel().getSessionInfo();
        
        base.fill(255);
        base.text("Ambient:" + session.getAmbientTemp(), 20, 20);
        base.text("current hud page:" + session.getCurrentHudPage(), 20, 40);
        base.text("cloud level:" + session.getCloudLevel(), 20, 60);
        base.text("focused car:" + session.getFocusedCarIndex(), 20, 80);
        base.text("session end time:" + session.getSessionEndTime(), 20, 100);
        base.text("session time remaining:" + session.getSessionTime(), 20, 120);
        base.text("track temp:" + session.getTrackTemp(), 20, 140);

    }

}
