/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_MEDIUM_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;

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
    /**
     * Size of the collapse box.
     */
    private final int COLLAPSE_BOX_SIZE = LINE_HEIGHT - 8;
    /**
     * X position of the collapse box.
     */
    private final int COLLAPSE_BOX_X = 14;
    /**
     * Y position of the collapse box.
     */
    private final int COLLAPSE_BOX_Y = (LINE_HEIGHT - COLLAPSE_BOX_SIZE) / 2;
    /**
     * True if the mouse is over the collapse button.
     */
    private boolean mouseOverCollapseButton = false;
    /**
     * The expanded width.
     */
    private float expandedWidth;
    /**
     * The expanded height.
     */
    private float expandedHeight;
    /**
     * Action for when the collapse state changes.
     */
    private Runnable stateChangeAction = () -> {
    };
    /**
     * Collapse animation.
     */
    private final LPAnimationTask collapseAnimation
            = new LPAnimationTask(this::collapseAnimationFunction, 200);
    /**
     * Expand animation.
     */
    private final LPAnimationTask expandAnimation
            = new LPAnimationTask(this::expandAnimationFunction, 200);
    /**
     * The collapse state for this panel. 0 is fully collapsed, 1 is fully
     * expanded.
     */
    private float collapseValue;

    public LPCollapsablePanel(String title) {
        addAnimationTask(collapseAnimation);
        addAnimationTask(expandAnimation);
        this.title = title;
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_MEDIUM_DARK_GRAY);
        applet.rect(0, 0, getWidth(), LINE_HEIGHT);

        if (mouseOverCollapseButton) {
            applet.fill(COLOR_RED);
            applet.rect(COLLAPSE_BOX_X, COLLAPSE_BOX_Y, COLLAPSE_BOX_SIZE, COLLAPSE_BOX_SIZE);
        }

        float triangleHeight = TEXT_SIZE;
        float triangleLength = TEXT_SIZE;

        applet.fill(255);
        applet.beginShape();
        if (isCollapsed()) {
            applet.vertex(COLLAPSE_BOX_X + COLLAPSE_BOX_SIZE / 2f, COLLAPSE_BOX_Y + (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
            applet.vertex(COLLAPSE_BOX_X + (COLLAPSE_BOX_SIZE - triangleLength) / 2f, COLLAPSE_BOX_Y + COLLAPSE_BOX_SIZE - (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
            applet.vertex(COLLAPSE_BOX_X + (COLLAPSE_BOX_SIZE + triangleLength) / 2f, COLLAPSE_BOX_Y + COLLAPSE_BOX_SIZE - (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
        } else {
            applet.vertex(COLLAPSE_BOX_X + COLLAPSE_BOX_SIZE / 2f, COLLAPSE_BOX_Y + COLLAPSE_BOX_SIZE - (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
            applet.vertex(COLLAPSE_BOX_X + (COLLAPSE_BOX_SIZE - triangleLength) / 2f, COLLAPSE_BOX_Y + (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
            applet.vertex(COLLAPSE_BOX_X + (COLLAPSE_BOX_SIZE + triangleLength) / 2f, COLLAPSE_BOX_Y + (COLLAPSE_BOX_SIZE - triangleHeight) / 2);
        }
        applet.endShape();
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.fill(255);
        applet.stroke(255);
        applet.text(title, LINE_HEIGHT + 20, LINE_HEIGHT * 0.5f);

        applet.noStroke();
        if (collapseValue > 0) {
            applet.fill(COLOR_MEDIUM_DARK_GRAY);
            applet.rect(0, LINE_HEIGHT, getWidth(), getHeight());
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
        boolean newValue = false;
        if (x > COLLAPSE_BOX_X && x < COLLAPSE_BOX_X + COLLAPSE_BOX_SIZE
                && y > COLLAPSE_BOX_Y && y < COLLAPSE_BOX_Y + COLLAPSE_BOX_SIZE) {
            newValue = true;
        }
        if (newValue != mouseOverCollapseButton) {
            mouseOverCollapseButton = newValue;
            invalidate();
        }
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (x > COLLAPSE_BOX_X && x < COLLAPSE_BOX_X + COLLAPSE_BOX_SIZE
                && y > COLLAPSE_BOX_Y && y < COLLAPSE_BOX_Y + COLLAPSE_BOX_SIZE) {
            setCollapsedAnimate(!isCollapsed());
            mouseOverCollapseButton = false;
            invalidate();
            stateChangeAction.run();
        }
    }

    @Override
    public void setSize(float w, float h) {
        expandedHeight = h;
        expandedWidth = w;
        updateSize();
    }

    public void setCollapsed(boolean collapsed) {
        collapseValue = collapsed ? 0 : 1;
        getComponents().forEach((c) -> c.setVisible(!collapsed));
        updateSize();
    }

    public void setCollapsedAnimate(boolean collapsed) {
        if (collapsed) {
            collapseAnimation.restart();
        } else {
            getComponents().forEach((c) -> c.setVisible(!collapsed));
            expandAnimation.restart();
        }
    }

    public boolean isCollapsed() {
        return collapseValue < 0.5f;
    }

    public void setAction(Runnable a) {
        stateChangeAction = a;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void updateSize() {
        float height = LINE_HEIGHT * (1 - collapseValue)
                + expandedHeight * collapseValue;
        super.setSize(expandedWidth, height);
        invalidateParent();
    }

    public void collapseAnimationFunction(LPAnimationTask task, float dt) {
        collapseValue = 1 - collapseAnimation.getProgressNormal();
        collapseValue = collapseValue * collapseValue;
        updateSize();

        if (collapseAnimation.isFinished()) {
            getComponents().forEach((c) -> c.setVisible(false));
        }
    }

    public void expandAnimationFunction(LPAnimationTask task, float dt) {
        float t = expandAnimation.getProgressNormal() - 1;
        collapseValue = -(t * t) + 1;
        updateSize();
    }

}
