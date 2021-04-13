/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
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
        selected = !selected;
        invalidate();
    }

    @Override
    public void draw() {
        applet.stroke(COLOR_WHITE);
        if (selected) {
            applet.fill(COLOR_WHITE);
        } else {
            applet.fill(COLOR_DARK_GRAY);

        }
        applet.strokeWeight(3);
        applet.rect(0, 0, TEXT_SIZE, TEXT_SIZE);
        applet.strokeWeight(1);
    }

}
