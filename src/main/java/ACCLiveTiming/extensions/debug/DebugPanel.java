/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.debug;

import ACCLiveTiming.extensions.ExtensionPanel;
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
    public void drawPanel() {
        SessionInfo session = extension.getModel().getSessionInfo();
        
        layer.fill(255);
        layer.text("Ambient:" + session.getAmbientTemp(), 20, 20);
        layer.text("current hud page:" + session.getCurrentHudPage(), 20, 40);
        layer.text("cloud level:" + session.getCloudLevel(), 20, 60);
        layer.text("focused car:" + session.getFocusedCarIndex(), 20, 80);
        layer.text("session end time:" + session.getSessionEndTime(), 20, 100);
        layer.text("session time remaining:" + session.getSessionTime(), 20, 120);
        layer.text("track temp:" + session.getTrackTemp(), 20, 140);

    }

}
