/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.incidents;

import base.screen.extensions.AccClientExtension;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class IncidentExtensionFactory
    implements base.ACCLiveTimingExtensionFactory{

    @Override
    public String getName() {
        return "Incident extension";
    }

    @Override
    public AccClientExtension createExtension() {
        return new IncidentExtension();
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }
    
    
}