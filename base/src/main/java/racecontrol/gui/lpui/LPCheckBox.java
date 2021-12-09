/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import java.util.function.Consumer;
import processing.core.PApplet;
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

    private Consumer<Boolean> changeAction = (b) -> {
    };

    public LPCheckBox() {
        setSize(TEXT_SIZE, TEXT_SIZE);
    }

    @Override
    public void onMouseEnter() {
        getApplet().cursor(HAND);
    }

    @Override
    public void onMouseLeave() {
        getApplet().cursor(ARROW);
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (isEnabled()) {
            selected = !selected;
            changeAction.accept(selected);
            invalidate();
        }
    }

    @Override
    public void draw(PApplet applet) {

        applet.fill(COLOR_WHITE);
        applet.noStroke();
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
        if (selected != state) {
            selected = state;
            changeAction.accept(selected);
            invalidate();
        }
    }

    public void setChangeAction(Consumer<Boolean> action) {
        this.changeAction = action;
    }

}
