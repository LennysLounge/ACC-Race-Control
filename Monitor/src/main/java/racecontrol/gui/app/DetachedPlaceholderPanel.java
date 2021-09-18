/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPComponent;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class DetachedPlaceholderPanel
        extends LPContainer {

    private String text = "hm";
    

    
    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        
        applet.fill(COLOR_WHITE);
        applet.textFont(LookAndFeel.fontRegular());
        applet.noStroke();
        applet.textAlign(CENTER, CENTER);
        applet.text(text, getWidth() / 2f, getHeight() / 2f);
    }

    public void setPanelName(String panelName){
        text = "The " + panelName + " panel is currently detached.";
    }

}
