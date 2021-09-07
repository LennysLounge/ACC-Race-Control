/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 *
 * @author Leonard
 */
public class LPBase extends PApplet {

    private int sizeWidth;
    private int sizeHeight;
    private LPComponent mousePressedTarget;
    private LPComponent base;

    public LPBase() {
        LPComponent.setApplet(this);
    }

    public void setComponent(LPComponent c) {
        base = c;
        c.setApplet(this);
        c.setParent(null);
    }

    @Override
    public void draw() {
        if (width != sizeWidth || height != sizeHeight) {
            onResize(width, height);
        }

        translate(base.getPosX(), base.getPosY());
        clip(0, 0, base.getWidth(), base.getHeight());
        base.drawInternal();
        noClip();
        translate(-base.getPosX(), -base.getPosY());

    }

    protected void onResize(int w, int h) {
        sizeWidth = w;
        sizeHeight = h;
        base.setSize(w, h);
    }

    @Override
    public void mousePressed() {
        LPComponent clickedComponent = base.onMousePressedInternal(mouseX, mouseY, mouseButton);

        //invalidate current focused component.
        if (LPContainer.getFocused() != null) {
            LPContainer.getFocused().invalidate();
        }
        mousePressedTarget = clickedComponent;
        if (clickedComponent != null) {
            LPComponent.setFocused(clickedComponent);
            clickedComponent.invalidate();
        }
    }

    @Override
    public void mouseReleased() {
        if (mousePressedTarget != null) {
            mousePressedTarget.onMouseReleasedInternal(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        base.onMouseScrollInternal(mouseX, mouseY, event.getCount());
    }

    @Override
    public void mouseMoved() {
        base.onMouseMoveInternal(mouseX, mouseY);
    }

    @Override
    public void mouseDragged() {
        base.onMouseMoveInternal(mouseX, mouseY);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        base.onKeyPressedInternal(event);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        base.onKeyReleasedInternal(event);
    }

}
