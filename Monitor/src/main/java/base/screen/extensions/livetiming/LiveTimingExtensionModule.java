/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.ACCLiveTimingExtensionModule;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtensionModule
        implements ACCLiveTimingExtensionModule {
    
    private LiveTimingExtension extension;
    private LiveTimingPanel panel;
    
    public LiveTimingExtensionModule(){
        extension = new LiveTimingExtension();
        panel = new LiveTimingPanel(extension);
    }

    @Override
    public String getName() {
        return "Live Timing Extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

    @Override
    public LPContainer getExtensionPanel() {
        return panel;
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }

}
