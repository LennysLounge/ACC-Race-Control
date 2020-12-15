/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.extensions.incidents;

import acclivetiming.monitor.extensions.AccClientExtension;
import acclivetiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class IncidentExtensionModule
    implements acclivetiming.ACCLiveTimingExtensionModule{
    
    private IncidentExtension extension;
    private IncidentPanel panel;
    
    public IncidentExtensionModule(){
        extension = new IncidentExtension();
        panel = new IncidentPanel(extension);
    }

    @Override
    public String getName() {
        return "Incident extension";
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
