/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.debug;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class DebugExtensionFactory
        implements ACCLiveTimingExtensionFactory {
    
    private DebugConfigPanel configPanel;
    
    public DebugExtensionFactory(){
        configPanel = new DebugConfigPanel();
    }

    @Override
    public String getName() {
        return "Debug";
    }

    @Override
    public AccClientExtension createExtension() {
        return new DebugExtension();
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return configPanel;
    }

}
