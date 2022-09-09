/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.test;

import processing.core.PApplet;
import racecontrol.gui.lpui.LPComponent;

/**
 *
 * @author Leonard
 */
public class BluePanel
        extends LPComponent {

    @Override
    public void draw(PApplet applet) {
        applet.fill(0, 0, 255);
        applet.rect(0, 0, getWidth(), getHeight());
    }

}
