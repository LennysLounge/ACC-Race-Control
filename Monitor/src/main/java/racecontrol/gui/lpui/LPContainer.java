/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import processing.event.KeyEvent;

/**
 *
 * @author Leonard
 */
public class LPContainer
        extends LPComponent {

    private List<LPComponent> components = new LinkedList<>();

    public void addComponent(LPComponent c) {
        components.add(c);
        c.setParent(this);
    }

    public void removeComponent(LPComponent c) {
        components.remove(c);
    }

    public List<LPComponent> getComponents() {
        return components;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        components.forEach(c -> c.invalidate());
    }

    @Override
    public void drawInternal(PApplet applet) {
        if (!isVisible()) {
            return;
        }

        super.drawInternal(applet);

        for (LPComponent c : components) {
            applet.translate(c.getPosX(), c.getPosY());
            applet.clip(0, 0, c.getWidth(), c.getHeight());
            c.drawInternal(applet);
            applet.noClip();
            applet.translate(-c.getPosX(), -c.getPosY());
        }
    }

    @Override
    public LPComponent onMousePressedInternal(int mouseX, int mouseY, int mouseButton) {
        if (!isVisible()) {
            return null;
        }
        LPComponent clickedComponent = null;
        if (mouseX >= getPosX() && mouseX < getPosX() + getWidth()
                && mouseY >= getPosY() && mouseY < getPosY() + getHeight()) {
            clickedComponent = this;

            //find if child components where clicked
            LPComponent child = null;
            for (LPComponent c : components) {
                child = c.onMousePressedInternal((int) (mouseX - getPosX()),
                        (int) (mouseY - getPosY()),
                        mouseButton);
                if (child != null) {
                    clickedComponent = child;
                    break;
                }
            }
            //run mouse pressed event for this component.
            onMousePressed((int) (mouseX - getPosX()),
                    (int) (mouseY - getPosY()), mouseButton);
        }
        return clickedComponent;
    }

    @Override
    public LPComponent onMouseScrollInternal(int mouseX, int mouseY, int scrolDir) {
        if (!isVisible()) {
            return null;
        }
        LPComponent target = null;
        if (mouseX > getPosX() && mouseX < getPosX() + getWidth()
                && mouseY > getPosY() && mouseY < getPosY() + getHeight()) {
            target = this;

            //find if child components where clicked
            LPComponent child = null;
            for (LPComponent c : components) {
                child = c.onMouseScrollInternal((int) (mouseX - getPosX()),
                        (int) (mouseY - getPosY()),
                        scrolDir);
                if (child != null) {
                    target = child;
                    break;
                }
            }
            //run mouse pressed event for this component.
            onMouseScroll(scrolDir);
        }
        return target;
    }

    @Override
    public void onMouseMoveInternal(int x, int y) {
        if (!isVisible()) {
            return;
        }
        super.onMouseMoveInternal(x, y);
        if (isMouseOver()) {
            for (LPComponent c : components) {
                c.onMouseMoveInternal((int) (x - getPosX()), (int) (y - getPosY()));
            }
        }
    }

    @Override
    public void onMouseLeaveInternal() {
        if (!isVisible()) {
            return;
        }
        super.onMouseLeaveInternal();
        for (LPComponent c : components) {
            c.onMouseLeaveInternal();
        }
    }

    @Override
    public void onKeyPressedInternal(KeyEvent event) {
        if (!isVisible()) {
            return;
        }
        super.onKeyPressedInternal(event);
        for (LPComponent c : components) {
            c.onKeyPressedInternal(event);
        }
    }

    @Override
    public void onKeyReleasedInternal(KeyEvent event) {
        if (!isVisible()) {
            return;
        }
        super.onKeyReleasedInternal(event);
        for (LPComponent c : components) {
            c.onKeyReleasedInternal(event);
        }
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        for (LPComponent c : components) {
            c.setEnabled(isEnabled);
        }
        super.setEnabled(isEnabled);
    }

}
