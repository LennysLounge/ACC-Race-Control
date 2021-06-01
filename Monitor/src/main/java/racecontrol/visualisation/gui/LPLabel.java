/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation.gui;

import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_GRAY;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import static racecontrol.visualisation.gui.LPComponent.applet;
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
        applet.textFont(LookAndFeel.fontMedium());
        this.setSize(applet.textWidth(text), LookAndFeel.LINE_HEIGHT);
        invalidate();
    }

    public void setHAlign(int hAlign) {
        this.hAlign = hAlign;
    }

    @Override
    public void draw() {
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
