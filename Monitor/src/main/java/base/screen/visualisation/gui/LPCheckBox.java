/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_RED;
import static base.screen.visualisation.LookAndFeel.COLOR_LIGHT_GRAY;
import static base.screen.visualisation.LookAndFeel.COLOR_RED;
import static base.screen.visualisation.LookAndFeel.COLOR_WHITE;
import static base.screen.visualisation.LookAndFeel.TEXT_SIZE;
import static base.screen.visualisation.gui.LPComponent.applet;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.HAND;

/**
 *
 * @author Leonard
 */
public class LPCheckBox
        extends LPComponent {

    /**
     * True if the check box is checked.
     */
    private boolean selected;

    public LPCheckBox() {
        setSize(TEXT_SIZE, TEXT_SIZE);
    }

    @Override
    public void onMouseEnter() {
        applet.cursor(HAND);
    }

    @Override
    public void onMouseLeave() {
        applet.cursor(ARROW);
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (isEnabled()) {
            selected = !selected;
            invalidate();
        }
    }

    @Override
    public void draw() {

        applet.fill(COLOR_WHITE);
        applet.rect(0, 0, TEXT_SIZE, TEXT_SIZE);

        if (selected) {
            applet.fill(COLOR_RED);
        } else {
            applet.fill(COLOR_DARK_GRAY);

        }
        applet.rect(3, 3, TEXT_SIZE - 6, TEXT_SIZE - 6);
        applet.strokeWeight(1);

        if (!isEnabled()) {
            applet.noStroke();
            applet.fill(0, 0, 0, 150);
            applet.rect(0, 0, TEXT_SIZE, TEXT_SIZE);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean state) {
        selected = state;
        invalidate();
    }

}
