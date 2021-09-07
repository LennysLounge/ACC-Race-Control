/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import static java.util.Objects.requireNonNull;
import processing.core.PApplet;
import processing.event.KeyEvent;

/**
 *
 * @author Leonard
 */
public class LPComponent {

    /**
     * The component that currently has focus.
     */
    private static LPComponent focused;
    /**
     * Reference to the PApplet.
     */
    protected static PApplet applet;
    /**
     * horizontal position in the applet.
     */
    private float posX;
    /**
     * Vertical position in the applet.
     */
    private float posY;
    /**
     * Width of this component.
     */
    private float width;
    /**
     * Height of this component.
     */
    private float height;
    /**
     * Flags that this component needs to be redrawn.
     */
    private boolean isInvalid = false;
    /**
     * Name for this component.
     */
    private String name = "";
    /**
     * The parent component for this component.
     */
    private LPComponent parent;
    /**
     * Indicates that the mouse in ontop of this component.
     */
    private boolean isMouseOntop = false;
    /**
     * Horizontal mouse position on this component.
     */
    private int mouseX;
    /**
     * Vertical mouse position on this component.
     */
    private int mouseY;
    /**
     * True if input to this component is enabled.
     */
    private boolean isEnabled = true;
    /**
     * True if the component is currently visible and should be drawn.
     */
    private boolean visible = true;

    /**
     * Creates a new instance.
     */
    public LPComponent() {
    }

    /**
     * Sets the position of this component.
     *
     * @param x X-position.
     * @param y Y-position.
     */
    public void setPosition(float x, float y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * Returns the X-position.
     *
     * @return the X-position.
     */
    public float getPosX() {
        return posX;
    }

    /**
     * Returns the Y-position.
     *
     * @return the Y-position.
     */
    public float getPosY() {
        return posY;
    }

    /**
     * Sets the size for this component.
     *
     * @param w Width.
     * @param h Height.
     */
    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
        onResize(w, h);
        invalidate();
    }

    /**
     * Returns the current width of this component.
     *
     * @return the width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the current height of this component.
     *
     * @return the height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Set the base PApplet.
     *
     * @param a PApplet.
     */
    public static void setApplet(PApplet a) {
        applet = a;
    }

    /**
     * Set the parent element for this component.
     *
     * @param parent parent component.
     */
    protected void setParent(LPComponent parent) {
        this.parent = parent;
    }

    /**
     * Set name for this component.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this component.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Invalidate this component to mark it to be redrawn.
     */
    public void invalidate() {
        isInvalid = true;
    }
    
    /**
     * Invalidates the parent of this component.
     */
    public void invalidateParent(){
        parent.invalidate();
    }

    /**
     * Returns if the component is invalid.
     *
     * @return
     */
    public boolean isInvalid() {
        return isInvalid;
    }

    /**
     * Returns true if this component is the one currently focused.
     *
     * @return
     */
    protected boolean isFocused() {
        return this == focused;
    }

    /**
     * Sets the focused component.
     *
     * @param comp the focused component.
     */
    protected static void setFocused(LPComponent comp) {
        requireNonNull(comp, "comp");
        if (comp == focused) {
            return;
        }
        if (focused != null) {
            focused.onFocusLost();
        }
        focused = comp;
        focused.onFocusGained();
    }

    /**
     * Returns the currently focused component.
     *
     * @return the focused component.
     */
    protected static LPComponent getFocused() {
        return focused;
    }

    /**
     * Event for when a component gains focus. Override this event to be able to
     * react to it.
     */
    public void onFocusGained() {
    }

    /**
     * Event for when a component loses focus. Override this event to be able to
     * react to it.
     */
    public void onFocusLost() {
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        if (this.isEnabled != isEnabled) {
            this.isEnabled = isEnabled;
            if (isEnabled) {
                onEnabled();
            } else {
                onDisabled();
            }
            invalidate();
        }
    }

    /**
     * Event for when a componentis enabled or disabled. Override this event to
     * be able to react to it.
     */
    public void onEnabled() {
    }

    /**
     * Event for when a componentis enabled or disabled. Override this event to
     * be able to react to it.
     */
    public void onDisabled() {
    }

    /**
     * Sets the visibility status for this component.
     *
     * @param visible True if the component should be drawn.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        invalidate();
    }
    
    public boolean isVisible(){
        return visible;
    }

    /**
     * Draws this component. Used internaly.
     */
    protected void drawInternal() {
        if (isInvalid && visible) {
            draw();
            isInvalid = false;
        }
    }

    /**
     * Override this method to draw this component.
     */
    public void draw() {
    }

    /**
     * Mouse pressed event. Used internaly, do not call directly.
     *
     * @param mouseX X-position of the mouse click.
     * @param mouseY Y-position of the mouse click.
     * @param mouseButton which mouse button was pressed.
     * @return Returns the top most component that was pressed.
     */
    protected LPComponent onMousePressedInternal(int mouseX, int mouseY, int mouseButton) {
        if(!isVisible()){
            return null;
        }
        LPComponent clickedComponent = null;
        if (mouseX >= posX && mouseX < posX + width
                && mouseY >= posY && mouseY < posY + height) {
            clickedComponent = this;

            //run mouse pressed event for this component.
            onMousePressed((int) (mouseX - posX), (int) (mouseY - posY), mouseButton);
        }
        return clickedComponent;
    }

    /**
     * Mouse button pressed event. Override this event to be able to react to
     * it.
     *
     * @param x the x position of the cursor on this component.
     * @param y the y position of the corsor on this component.
     * @param button the button type for the press. either LEFT, RIGHT or CENTER
     */
    public void onMousePressed(int x, int y, int button) {
    }

    /**
     * Mouse released event. Used internaly, do not call directly.
     *
     * @param mouseX X-position of the mouse click.
     * @param mouseY Y-position of the mouse click.
     * @param mouseButton which mouse button was pressed.
     */
    public void onMouseReleasedInternal(int mouseX, int mouseY, int mouseButton) {
        if(!isVisible()){
            return;
        }
        onMouseReleased(mouseX, mouseY, mouseButton);
        if (parent != null) {
            parent.onMouseReleasedInternal(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Mouse button released event. Override this event to be able to react to
     * it.
     *
     * @param x the x position of the cursor on this component.
     * @param y the y position of the corsor on this component.
     * @param button the button type for the press. either LEFT, RIGHT or CENTER
     */
    public void onMouseReleased(int x, int y, int button) {
    }

    /**
     * Mouse scroll event. Used internaly, do not call directly.
     *
     * @param mouseX X-position of the mouse click.
     * @param mouseY Y-position of the mouse click.
     * @param scrolDir direction of the scroll.
     */
    public LPComponent onMouseScrollInternal(int mouseX, int mouseY, int scrolDir) {
        if(!isVisible()){
            return null;
        }
        LPComponent target = null;
        if (mouseX > posX && mouseX < posX + width
                && mouseY > posY && mouseY < posY + height) {
            target = this;
            onMouseScroll(scrolDir);

        }
        return target;
    }

    /**
     * Mouse scroll event. Override this event to be able to react to it.
     *
     * @param scrollDir Direction of the scroll.
     */
    public void onMouseScroll(int scrollDir) {
    }

    /**
     * Resize event.
     *
     * @param w new width of this component.
     * @param h new height of this component.
     */
    public void onResize(float w, float h) {

    }

    /**
     * Event for when the mouse enters the bounding box of this component.
     * Override this event to be able to react to it.
     */
    public void onMouseEnter() {
    }

    /**
     * Event for when the mouse leaves the bounding box of this component.
     * Override this event to be able to react to it.
     */
    public void onMouseLeave() {
    }

    /**
     * Event for when the mouse moves across this compoennt. Override this event
     * to be able to react to it.
     *
     * @param x the current x position for the mouse.
     * @param y the current y position for the mouse.
     */
    public void onMouseMove(int x, int y) {
    }

    /**
     * Returns the current mouse x position.
     *
     * @return the current mouse x position.
     */
    public int mouseX() {
        return mouseX;
    }

    /**
     * Returns the current mouse y position.
     *
     * @return the mouse y position.
     */
    public int mouseY() {
        return mouseY;
    }

    /**
     * Returns true when the mouse if currently over this component.
     *
     * @return
     */
    public boolean isMouseOver() {
        return isMouseOntop;
    }

    /**
     * Mouse enter event. Used internally, do not call directly.
     */
    public void onMouseEnterInternal() {
        if(!isVisible()){
            return;
        }
        onMouseEnter();
    }

    /**
     * Mouse leave event. Used internally, do not call directly.
     */
    public void onMouseLeaveInternal() {
        if(!isVisible()){
            return;
        }
        onMouseLeave();
    }

    /**
     * Mouse move event. Used internally, do not call directly.
     *
     * @param x the current x position for the mouse.
     * @param y the current y position for the mouse.
     */
    public void onMouseMoveInternal(int x, int y) {
        if(!isVisible()){
            return;
        }
        if (x > posX && x < posX + width
                && y > posY && y < posY + height) {
            if (!isMouseOntop) {
                isMouseOntop = true;
                onMouseEnterInternal();
            }
            mouseX = x;
            mouseY = y;
            onMouseMove((int) (x - posX), (int) (y - posY));
        } else {
            if (isMouseOntop) {
                isMouseOntop = false;
                onMouseLeaveInternal();
            }
        }
    }

    /**
     * Key pressed event. Used internally, do not call directly.
     *
     * @param event The key event
     */
    public void onKeyPressedInternal(KeyEvent event) {
        if(!isVisible()){
            return;
        }
        onKeyPressed(event);
    }

    /**
     * Key pressed event. Override this event to be able to react to it.
     *
     * @param event the key event.
     */
    public void onKeyPressed(KeyEvent event) {
    }

    /**
     * Key released event. Used internally, do not call directly.
     *
     * @param event the key event.
     */
    public void onKeyReleasedInternal(KeyEvent event) {
        if(!isVisible()){
            return;
        }
        onKeyReleased(event);
    }

    /**
     * Key released event. Override this event to be able to react to it.
     *
     * @param event the key event.
     */
    public void onKeyReleased(KeyEvent event) {
    }

}
