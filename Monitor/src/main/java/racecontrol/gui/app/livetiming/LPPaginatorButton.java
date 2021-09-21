/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPButton;

/**
 *
 * @author Leonard
 */
public class LPPaginatorButton extends LPButton {

    private boolean left;

    public LPPaginatorButton(boolean left) {
        super("");
        this.left = left;
    }

    @Override
    public void draw(PApplet applet) {
        if (isMouseOver()) {
            applet.fill(COLOR_RED);
        } else {
            applet.fill(COLOR_DARK_GRAY);
        }
        applet.rect(2, 2, getWidth() - 4, getHeight() - 4);

        applet.stroke(COLOR_WHITE);
        applet.strokeWeight(3);
        float size = 10;
        float dir = left ? size : -size;
        float x = getWidth() / 2f - dir / 2f;
        float y = getHeight() / 2f;
        applet.line(x, y, x + dir, y + dir);
        applet.line(x, y, x + dir, y - dir);

        applet.noStroke();
        applet.strokeWeight(1);
    }

}
