/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.LEFT;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;

/**
 *
 * @author Leonard
 */
public class LPScrollPanel
        extends LPContainer {

    private final static Logger LOG = Logger.getLogger(LPScrollPanel.class.getName());

    private LPComponent component;

    private float animatedScroll = 0;
    private float scroll = 0;

    private boolean scrollbarVisible = false;
    private float scrollbarWidth = 20;
    private float scrollbarHeight = 0;
    private float scrollbarPosition = 0;

    private boolean mouseOverScrollbar = false;
    private boolean mouseDragged = false;
    private float mouseDragHome = 0;
    private float mouseDragScrollHome = 0;

    /**
     * Animation task for smooth scrolling.
     */
    private final LPAnimationTask scrollAnimation
            = new LPAnimationTask(this::scrollAnimationFunction, 200);
    /**
     * scroll value where to start the animation.
     */
    private float scrollAnimationStart;
    /**
     * Scroll value where to end the animation.
     */
    private float scrollAnimationEnd;
    /**
     * Scroll velocity to start the animation.
     */
    private float scrollAnimationStartVelocity;
    /**
     * Current animation scroll velocity.
     */
    private float scrollAnimationCurrentVelocity;

    public LPScrollPanel() {
        addAnimationTask(scrollAnimation);
    }

    @Override
    public void addComponent(LPComponent component) {
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        if (component == null) {
            return;
        }
        if (!scrollbarVisible) {
            return;
        }

        applet.stroke(COLOR_GRAY);
        applet.line(scrollbarWidth / 2, 0, scrollbarWidth / 2, getHeight());
        applet.noStroke();

        applet.fill(COLOR_RED);
        if (mouseOverScrollbar || mouseDragged) {
            applet.fill(COLOR_WHITE);
        }
        float padding = scrollbarWidth * 0.25f;
        applet.rect(padding, scrollbarPosition, scrollbarWidth - padding * 2, scrollbarHeight);

    }

    public void setComponent(LPComponent c) {
        if (component != null) {
            removeComponent(component);
        }
        component = c;
        super.addComponent(component);
        component.setSize(getWidth(), component.getHeight());
        component.invalidate();
        setScroll(0);
        invalidate();
    }

    @Override
    public void onResize(float w, float h) {
        if (component != null) {
            component.setSize(getWidth(), component.getHeight());
            updateScroll();
        }
    }

    @Override
    public void onMouseScroll(int scrollDir) {
        setScrollSmooth(scroll + LINE_HEIGHT * scrollDir);
    }

    @Override
    public void onMouseMove(int x, int y) {
        boolean newState = false;
        if (scrollbarVisible && x < scrollbarWidth) {
            newState = true;
        }
        if (newState != mouseOverScrollbar) {
            mouseOverScrollbar = newState;
            invalidate();
        }
        if (mouseDragged) {
            float mouseDif = y - mouseDragHome;
            float scrollDif = mouseDif * component.getHeight() / getHeight();
            setScrollDirect(mouseDragScrollHome + scrollDif);
        }
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (button == LEFT) {
            if (x < scrollbarWidth && y > scrollbarPosition
                    && y < scrollbarPosition + scrollbarHeight) {
                mouseDragged = true;
                mouseDragHome = y;
                mouseDragScrollHome = animatedScroll;
            }
        }
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        mouseDragged = false;
        invalidate();
    }

    @Override
    public void onMouseLeave() {
        if (mouseOverScrollbar) {
            mouseOverScrollbar = false;
            invalidate();
        }
    }

    private void setScroll(float scroll) {
        if (scroll < 0) {
            scroll = 0;
        }
        float maxScroll = component.getHeight() - getHeight();
        if (scroll > maxScroll) {
            scroll = maxScroll;
        }
        if(this.scroll < 0){
            this.scroll = 0;
        }
        if(this.scroll > maxScroll){
            this.scroll = maxScroll;
        }
        this.animatedScroll = scroll;
        updateScroll();
    }

    private void updateScroll() {
        if (component == null) {
            return;
        }
        scrollbarHeight = (int) (getHeight() * (getHeight() / component.getHeight()));
        scrollbarPosition = (int) (getHeight() * (this.animatedScroll / component.getHeight()));
        scrollbarVisible = (component.getHeight() > getHeight());

        component.setPosition(scrollbarVisible ? scrollbarWidth : 0, -animatedScroll);
        invalidate();
    }

    private void scrollAnimationFunction(LPAnimationTask task, int dt) {
        float a = scrollAnimationStartVelocity - 2;
        float b = 3 - 2 * scrollAnimationStartVelocity;
        float c = scrollAnimationStartVelocity;
        float x = scrollAnimation.getProgressNormal();
        float t = a * x * x * x + b * x * x + c * x;
        scrollAnimationCurrentVelocity = 3 * a * x * x + 2 * b * x + c;
        setScroll(scrollAnimationStart * (1 - t) + scrollAnimationEnd * t);
        invalidate();
    }

    public void setScrollSmooth(float newScroll) {
        if (scroll != newScroll) {
            scroll = newScroll;
            scrollAnimationStart = animatedScroll;
            scrollAnimationEnd = newScroll;
            scrollAnimationStartVelocity = scrollAnimationCurrentVelocity;
            scrollAnimation.restart();
            invalidate();
        }
    }

    public void setScrollDirect(float newScroll) {
        if (scroll != newScroll) {
            scrollAnimation.stop();
            scroll = newScroll;
            animatedScroll = newScroll;
            setScroll(newScroll);
            invalidate();
        }
    }

}
