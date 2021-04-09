/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.debug;

import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.gui.LPButton;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class DebugPanel
        extends LPContainer {
    
    LPButton button = new LPButton("Button");

    public DebugPanel() {
        setName("Debug");
        
        button.setSize(200, LookAndFeel.LINE_HEIGHT);
        button.setPosition(200, 200);
        
        addComponent(button);
    }
    
    @Override
    public void draw(){
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

}
