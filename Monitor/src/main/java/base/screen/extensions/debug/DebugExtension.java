/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.debug;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *  A Basic Extension to test stuff out with.
 * @author Leonard
 */
public class DebugExtension implements AccClientExtension{
    
    DebugPanel panel;
    
    public DebugExtension(){
        this.panel = new DebugPanel();
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void removeExtension() {
    }
    
}
