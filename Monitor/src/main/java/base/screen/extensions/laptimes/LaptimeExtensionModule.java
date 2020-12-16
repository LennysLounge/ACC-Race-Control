/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.laptimes;

import base.ACCLiveTimingExtensionModule;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LaptimeExtensionModule
    implements ACCLiveTimingExtensionModule{
    
    private final LapTimeExtension extension;
    
    public LaptimeExtensionModule(){
        this.extension = new LapTimeExtension(false);
    }

    @Override
    public String getName() {
        return "Laptime extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
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
