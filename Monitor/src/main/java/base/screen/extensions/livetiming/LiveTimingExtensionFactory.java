/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming;

import base.screen.extensions.AccClientExtension;
import javax.swing.JPanel;
import base.ACCLiveTimingExtensionFactory;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtensionFactory
        implements ACCLiveTimingExtensionFactory {
    
    @Override
    public String getName() {
        return "Live Timing Extension";
    }

    @Override
    public AccClientExtension createExtension() {
        return new LiveTimingExtension();
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }

}
