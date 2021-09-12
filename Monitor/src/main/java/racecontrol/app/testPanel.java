/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import processing.core.PApplet;
import racecontrol.lpgui.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class testPanel extends LPContainer{
    
    @Override
    public void draw(PApplet applet){
        applet.fill(0);
        applet.rect(0,0,getWidth(), getHeight());
        applet.fill(255);
        applet.text("Hello World", 200, 200);
    }
    
}
