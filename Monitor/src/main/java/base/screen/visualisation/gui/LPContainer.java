/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import java.util.LinkedList;
import java.util.List;
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

    @Override
    public void invalidate() {
        super.invalidate();
        components.forEach(c -> c.invalidate());
    }

    @Override
    public void drawInternal() {
        super.drawInternal();

        for (LPComponent c : components) {
            applet.translate(c.getPosX(), c.getPosY());
            c.drawInternal();
            applet.translate(-c.getPosX(), -c.getPosY());
        }
    }

    @Override
    public LPComponent onMousePressedInternal(int mouseX, int mouseY, int mouseButton) {
        LPComponent clickedComponent = null;
        if (mouseX > getPosX() && mouseX < getPosX() + getWidth()
                && mouseY > getPosY() && mouseY < getPosY() + getHeight()) {
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
        super.onMouseMoveInternal(x, y);
        if (isMouseOntop()) {
            for (LPComponent c : components) {
                c.onMouseMoveInternal((int) (x - getPosX()), (int) (y - getPosY()));
            }
        }
    }
    
    @Override
    public void onMouseLeaveInternal(){
        super.onMouseLeaveInternal();
        for (LPComponent c : components) {
                c.onMouseLeaveInternal();
            }
    }
    
    @Override
    public void onKeyPressedInternal(KeyEvent event){
        super.onKeyPressedInternal(event);
        for(LPComponent c : components){
            c.onKeyPressedInternal(event);
        }
    }
    
    @Override
    public void onKeyReleasedInternal(KeyEvent event){
        super.onKeyReleasedInternal(event);
        for(LPComponent c : components){
            c.onKeyReleasedInternal(event);
        }
    }

}
