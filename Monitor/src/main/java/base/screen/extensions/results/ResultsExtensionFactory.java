/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.results;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class ResultsExtensionFactory 
    implements ACCLiveTimingExtensionFactory{

    @Override
    public String getName() {
        return "Results extension";
    }

    @Override
    public AccClientExtension createExtension() {
        return new ResultsExtension();
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }
    
}
