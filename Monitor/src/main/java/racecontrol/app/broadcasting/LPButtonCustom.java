/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.broadcasting;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_GRAY;
import static racecontrol.LookAndFeel.COLOR_RED;
import racecontrol.lpgui.gui.LPButton;

/**
 *
 * @author Leonard
 */
public class LPButtonCustom
        extends LPButton {

    private String text;

    public LPButtonCustom(String text) {
        super(text);
        this.text = text;
    }

    @Override
    public void draw(PApplet applet) {
        if (isEnabled()) {
            if (isMouseOver()) {
                applet.fill(COLOR_RED);
            } else {
                applet.fill(COLOR_GRAY);
            }
        } else {
            applet.fill(COLOR_RED);
        }

        applet.noStroke();
        applet.rect(0, 2, getWidth(), getHeight() - 4);
        
        applet.fill(LookAndFeel.COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(text, getWidth() / 2f, getHeight() / 2f);
    }

}
