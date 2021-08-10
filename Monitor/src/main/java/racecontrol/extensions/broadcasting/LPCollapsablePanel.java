/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_DARK_DARK_GRAY;
import static racecontrol.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.visualisation.LookAndFeel.COLOR_RED;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import static racecontrol.visualisation.LookAndFeel.TEXT_SIZE;
import racecontrol.visualisation.gui.LPContainer;

/**
 * A panel that is collapsable.
 *
 * @author Leonard
 */
public class LPCollapsablePanel
        extends LPContainer {

    /**
     * Title for this panel.
     */
    private String title;

    private final int BOX_SIZE = LINE_HEIGHT - 8;
    private final int BOX_X = 14;
    private final int BOX_Y = (LINE_HEIGHT - BOX_SIZE) / 2;

    /**
     * indicates that the mouse if over the collapse button.
     */
    private boolean mouseOver = false;

    /**
     * True if the panel is in the collapsed state.
     */
    private boolean collapsed = false;

    private float actualWidth;
    private float actualHeight;

    /**
     * Action for when the collapse state changes.
     */
    private Runnable stateChangeAction = () -> {
    };

    public LPCollapsablePanel(String title) {
        this.title = title;
    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_DARK_GRAY);
        applet.rect(0, 0, getWidth(), LINE_HEIGHT);

        if (mouseOver) {
            applet.fill(COLOR_RED);
            applet.rect(BOX_X, BOX_Y, BOX_SIZE, BOX_SIZE);
        }

        float triangleHeight = TEXT_SIZE;
        float triangleLength = TEXT_SIZE;

        applet.fill(255);
        applet.beginShape();
        if (collapsed) {
            applet.vertex(BOX_X + BOX_SIZE / 2f, BOX_Y + (BOX_SIZE - triangleHeight) / 2);
            applet.vertex(BOX_X + (BOX_SIZE - triangleLength) / 2f, BOX_Y + BOX_SIZE - (BOX_SIZE - triangleHeight) / 2);
            applet.vertex(BOX_X + (BOX_SIZE + triangleLength) / 2f, BOX_Y + BOX_SIZE - (BOX_SIZE - triangleHeight) / 2);
        } else {
            applet.vertex(BOX_X + BOX_SIZE / 2f, BOX_Y + BOX_SIZE - (BOX_SIZE - triangleHeight) / 2);
            applet.vertex(BOX_X + (BOX_SIZE - triangleLength) / 2f, BOX_Y + (BOX_SIZE - triangleHeight) / 2);
            applet.vertex(BOX_X + (BOX_SIZE + triangleLength) / 2f, BOX_Y + (BOX_SIZE - triangleHeight) / 2);
        }
        applet.endShape();
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.fill(255);
        applet.stroke(255);
        applet.text(title, LINE_HEIGHT + 20, LINE_HEIGHT * 0.5f);

        applet.noStroke();
        if (!collapsed) {
            applet.fill(COLOR_DARK_GRAY);
            applet.rect(0, LINE_HEIGHT, getWidth(), getHeight());
        }
    }

    @Override
    public void onMouseMove(int x, int y
    ) {
        boolean newValue = false;
        if (x > BOX_X && x < BOX_X + BOX_SIZE
                && y > BOX_Y && y < BOX_Y + BOX_SIZE) {
            newValue = true;
        }
        if (newValue != mouseOver) {
            mouseOver = newValue;
            invalidate();
        }
    }

    @Override
    public void onMousePressed(int x, int y, int button
    ) {
        if (x > BOX_X && x < BOX_X + BOX_SIZE
                && y > BOX_Y && y < BOX_Y + BOX_SIZE) {
            collapsed = !collapsed;
            setSize(actualWidth, actualHeight);
            getComponents().forEach((c) -> c.setVisible(!collapsed));
            mouseOver = false;
            invalidate();
            stateChangeAction.run();
        }
    }

    @Override
    public void setSize(float w, float h
    ) {
        actualWidth = w;
        actualHeight = h;
        if (collapsed) {
            super.setSize(w, LINE_HEIGHT);
        } else {
            super.setSize(w, h);
        }
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setAction(Runnable a) {
        stateChangeAction = a;
    }

}
