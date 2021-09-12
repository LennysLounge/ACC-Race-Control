/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import processing.core.PApplet;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LPLabel
        extends LPComponent {

    /**
     * The text the label is showing.
     */
    private String text;
    /**
     * Text align horizontal.
     */
    private int hAlign = LEFT;
    /**
     * Text align vertical.
     */
    private int vAlign = CENTER;

    public LPLabel(String text) {
        this.text = text;
        this.setSize(100, LINE_HEIGHT);
    }

    public void setText(String text) {
        this.text = text;
        getApplet().textFont(LookAndFeel.fontMedium());
        this.setSize(getApplet().textWidth(text), LookAndFeel.LINE_HEIGHT);
        invalidate();
    }

    public void setHAlign(int hAlign) {
        this.hAlign = hAlign;
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.noStroke();
        //applet.stroke(0);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(LookAndFeel.COLOR_WHITE);
        if (!isEnabled()) {
            applet.fill(COLOR_GRAY);
        }
        applet.textAlign(hAlign, vAlign);
        applet.textFont(LookAndFeel.fontRegular());
        if (hAlign == LEFT) {
            applet.text(text, 0, getHeight() / 2f);
        } else if (hAlign == CENTER) {
            applet.text(text, getWidth() / 2f, getHeight() / 2f);
        }
    }

}
