/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.ACCLiveTimingExtensionModule;
import base.screen.extensions.AccClientExtension;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtensionModule
        implements ACCLiveTimingExtensionModule {
    
    @Override
    public String getName() {
        return "Live Timing Extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return new LiveTimingExtension();
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }

}
