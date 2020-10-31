/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extension;

import ACCLiveTiming.monitor.ACCLiveTimingExtension;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class Extension implements ACCLiveTimingExtension{

    @Override
    public String getName() {
        return "Test extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return null;
    }

    @Override
    public LPContainer getExtensionPanel() {
        return null;
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }
    
}
