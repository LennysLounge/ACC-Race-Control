/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public interface ACCLiveTimingExtensionModule {
    /**
     * Returns the name for this extension.
     * @return The name.
     */
    public String getName();
    /**
     * Gives the client extension.
     * @return the client extension.
     */
    public AccClientExtension getExtension();
    /**
     * Gives the configuration dialog panel for this extension. Returns null
     * if this extension does not have a configuration panel.
     * @return 
     */
    public JPanel getExtensionConfigurationPanel();   
}
