/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface ACCLiveTimingExtensionFactory {

    /**
     * Returns the name for this extension.
     *
     * @return The name.
     */
    public String getName();

    /**
     * Creates the client extension.
     *
     */
    public void createExtension();

    /**
     * Removes the extension.
     */
    public void removeExtension();

    /**
     * Gives the configuration dialog panel for this extension. Returns null if
     * this extension does not have a configuration panel.
     *
     * @return
     */
    public LPContainer getExtensionConfigurationPanel();

    /**
     * returns the extension.
     *
     * @return
     */
    public AccClientExtension getExtension();
}
