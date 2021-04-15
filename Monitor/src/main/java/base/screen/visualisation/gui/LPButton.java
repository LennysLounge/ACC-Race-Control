/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import base.screen.visualisation.LookAndFeel;
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

    public LPButton(String text) {
        this.text = text;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
    
    public void setText(String text){
        this.text = text;
    }

    @Override
    public void draw() {
        applet.noStroke();
        applet.fill(LookAndFeel.COLOR_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        if (isMouseOver()) {
            applet.fill(LookAndFeel.COLOR_RED);
            applet.rect(0, 0, getWidth(), getHeight());
        }
        if (clicked) {
            applet.fill(LookAndFeel.TRANSPARENT_WHITE);
            applet.rect(0, 0, getWidth(), getHeight());
        }
        applet.fill(255);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(text, getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        clicked = true;
        invalidate();
        action.run();
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        clicked = false;
        invalidate();
    }

    @Override
    public void onMouseEnter() {
        invalidate();
        applet.cursor(HAND);
    }

    @Override
    public void onMouseLeave() {
        invalidate();
        applet.cursor(ARROW);
    }

}
