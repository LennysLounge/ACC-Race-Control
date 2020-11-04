/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.laptimes;

import ACCLiveTiming.ACCLiveTimingExtensionModule;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LaptimeExtensionModule
    implements ACCLiveTimingExtensionModule{

    @Override
    public String getName() {
        return "Laptime extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return new LapTimeExtension(false);
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
