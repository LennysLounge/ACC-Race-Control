/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import racecontrol.lpgui.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class testPanel extends LPContainer{
    
    @Override
    public void draw(){
        applet.fill(0);
        applet.rect(0,0,getWidth(), getHeight());
        applet.fill(255);
        applet.text("Hello World", 200, 200);
    }
    
}
