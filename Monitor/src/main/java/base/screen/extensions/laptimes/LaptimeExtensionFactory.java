/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.laptimes;

import base.screen.extensions.AccClientExtension;
import javax.swing.JPanel;
import base.ACCLiveTimingExtensionFactory;

/**
 *
 * @author Leonard
 */
public class LaptimeExtensionFactory
    implements ACCLiveTimingExtensionFactory{
    


    @Override
    public String getName() {
        return "Laptime extension";
    }

    @Override
    public AccClientExtension createExtension() {
        return new LapTimeExtension(false);
    }
    
    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }
    
}
