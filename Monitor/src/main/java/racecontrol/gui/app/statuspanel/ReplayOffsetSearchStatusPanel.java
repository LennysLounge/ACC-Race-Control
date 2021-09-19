/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.statuspanel;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GREEN;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class ReplayOffsetSearchStatusPanel
        extends LPContainer {

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_GREEN);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text("Searching for replay time, please wait", 10, LINE_HEIGHT * 0.5f);
    }

}
