/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions;

import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public interface AccClientExtension {
    /**
     * Returns the attached panel for this extension.
     * @return The attached panel.
     */
    public LPContainer getPanel();
    /**
     * Removes all necessary references related to this extension.
     */
    public void removeExtension();
}
