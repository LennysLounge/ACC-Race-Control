/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import processing.core.PApplet;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_GRAY;
import static racecontrol.LookAndFeel.COLOR_MEDIUM_DARK_GRAY;
import static racecontrol.LookAndFeel.COLOR_RED;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.HAND;

/**
 *
 * @author Leonard
 */
public class LPButton
        extends LPComponent {

    /**
     * The text to show on the button.
     */
    private String text;
    /**
     * The action to run here.
     */
    private Runnable action = () -> {
    };
    /**
     * Indicates that this button is clicked or not.
     */
    private boolean clicked = false;
    
    private int backgroundColor = COLOR_GRAY;

    public LPButton(String text) {
        this.text = text;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(PApplet applet) {
        if (isEnabled()) {
            if (isMouseOver()) {
                applet.fill(COLOR_RED);
            } else {
                applet.fill(backgroundColor);
            }
        } else {
            applet.fill(COLOR_MEDIUM_DARK_GRAY);
        }

        applet.noStroke();
        applet.rect(0, 2, getWidth(), getHeight() - 4);

        if (isEnabled()) {
            applet.fill(LookAndFeel.COLOR_WHITE);
        } else {
            applet.fill(LookAndFeel.COLOR_GRAY);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(text, getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (!isEnabled()) {
            return;
        }
        clicked = true;
        invalidate();
        action.run();
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        if (!isEnabled()) {
            return;
        }
        clicked = false;
        invalidate();
    }

    @Override
    public void onMouseEnter() {
        if (!isEnabled()) {
            return;
        }
        invalidate();
        getApplet().cursor(HAND);
    }

    @Override
    public void onMouseLeave() {
        if (!isEnabled()) {
            return;
        }
        invalidate();
        getApplet().cursor(ARROW);
    }
    
    public void setBackgroundColor(int color){
        backgroundColor = color;
        invalidate();
    }

}
