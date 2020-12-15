/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.visualisation.gui;

import acclivetiming.monitor.visualisation.LookAndFeel;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LPButton extends LPComponent {

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
    /**
     * Indicates that the mouse is over the button.
     */
    private boolean mouseOver = false;

    public LPButton(String text) {
        this.text = text;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void draw() {
        if (mouseOver) {
            applet.fill(LookAndFeel.TRANSPARENT_WHITE);
            applet.rect(0, 0, getWidth(), getHeight());
        }
        applet.fill(255);
        applet.textAlign(CENTER, CENTER);
        applet.text(text, getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void mousePressed(int x, int y, int button) {
        clicked = true;
        invalidate();
        action.run();
    }

    @Override
    public void mouseReleased(int x, int y, int button) {
        clicked = false;
        invalidate();
    }

    @Override
    public void onMouseEnter() {
        mouseOver = true;
    }

    @Override
    public void onMouseLeave() {
        mouseOver = false;
    }

}
