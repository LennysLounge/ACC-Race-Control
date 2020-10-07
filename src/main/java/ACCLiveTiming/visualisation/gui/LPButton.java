/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import ACCLiveTiming.visualisation.LookAndFeel;
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

    public LPButton(String text) {
        this.text = text;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void draw() {
        applet.stroke(LookAndFeel.get().COLOR_DARK_RED);
        applet.strokeWeight(2);
        if (clicked) {
            applet.fill(LookAndFeel.get().COLOR_DARK_RED);
        } else {
            applet.fill(LookAndFeel.get().COLOR_RED);
        }
        applet.rect(0, 0, getWidth(), getHeight(), 2);
        applet.fill(0);
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

}
