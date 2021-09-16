/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.virtualsafetycar;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.COLOR_ORANGE;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class VSCStatusPanel
        extends LPContainer {

    /**
     * Timestamp for when this panel was created
     */
    private long vscStart = 0;

    protected final LPButton settingsButton = new LPButton("Settings");

    public VSCStatusPanel() {
        settingsButton.setSize(100, LINE_HEIGHT);
        addComponent(settingsButton);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_ORANGE);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(COLOR_DARK_GRAY);
        applet.noStroke();
        applet.textFont(LookAndFeel.fontMedium());
        applet.textAlign(LEFT, CENTER);
        long time = System.currentTimeMillis() - vscStart;
        String text = "VSC Active!   Elapsed time: " + TimeUtils.asDuration(time);
        applet.text(text, 10, LINE_HEIGHT / 2);
    }

    @Override
    public void onResize(float w, float h) {
        settingsButton.setPosition(w - 110, 0);
    }
    
    public void setVSCStart(){
        vscStart = System.currentTimeMillis();
    }

}
