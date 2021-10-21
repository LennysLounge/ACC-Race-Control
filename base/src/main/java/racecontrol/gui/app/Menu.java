/**
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PImage;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_RED;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPAnimationTask;
import racecontrol.gui.lpui.LPComponent;

/**
 *
 * @author Leonard
 */
public class Menu
        extends LPComponent {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(Menu.class.getName());
    /**
     * Menu width when collapsed.
     */
    private final float itemSize = LINE_HEIGHT * 1.5f;
    /**
     * List of all MenuItems for this menu.
     */
    private final List<MenuItem> items = new ArrayList<>();
    /**
     * List of menu items from the bottom.
     */
    private final List<MenuItem> itemsBottom = new ArrayList<>();
    /**
     * Width when expanded.
     */
    private float expandedWidth;
    /**
     * Height when expanded.
     */
    private float expandedHeight;
    /**
     * Item of the mouse position.
     */
    private MenuItem mouseOverMenuItem;
    /**
     * Currently selected menu item.
     */
    private MenuItem selectedMenuItem;
    /**
     * Animation task to collapse the menu.
     */
    private final LPAnimationTask collapseAnimation
            = new LPAnimationTask(this::collapseAnimationFunction, 200);
    /**
     * Animation task to expand the menu.
     */
    private final LPAnimationTask expandAnimation
            = new LPAnimationTask(this::expandAnimationFunction, 200);
    /**
     * The collapse state for this menu. 0 is fully collapsed, 1 is fully
     * expanded.
     */
    private float collapseValue = 0;

    public Menu() {
        addAnimationTask(collapseAnimation);
        addAnimationTask(expandAnimation);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        float height = 0;
        for (MenuItem item : items) {
            drawMenuItem(applet, item, height);
            height += itemSize;
        }

        height = getHeight() - itemSize - 10;
        for (MenuItem item : itemsBottom) {
            drawMenuItem(applet, item, height);
            height -= itemSize;
        }
    }

    private void drawMenuItem(PApplet applet, MenuItem item, float height) {
        boolean isMouseOver = item == mouseOverMenuItem;
        boolean isSelected = item == selectedMenuItem;
        if (isMouseOver) {
            applet.fill(COLOR_DARK_RED);
            applet.rect(0, height, getWidth(), itemSize);
        }
        if (isSelected) {
            applet.fill(COLOR_RED);
            applet.rect(0, height, itemSize * 0.1f, itemSize);
        }

        //draw icon
        if (item.getIcon() != null) {
            applet.tint((isSelected || isMouseOver) ? 255 : 100);
            item.getIcon().resize((int) (itemSize * 0.6f), (int) (itemSize * 0.6f));
            applet.image(item.getIcon(), itemSize * 0.3f, height + itemSize * 0.2f);
        } else {
            applet.noFill();
            applet.stroke(COLOR_WHITE);
            applet.strokeWeight(3);
            applet.rect(itemSize * 0.3f, height + itemSize * 0.2f, itemSize * 0.6f, itemSize * 0.6f);
            applet.strokeWeight(1);
            applet.noStroke();
        }

        if (collapseValue > 0) {
            //draw text
            applet.textAlign(LEFT, CENTER);
            applet.fill((isSelected || isMouseOver) ? COLOR_WHITE : 100);
            applet.textFont(LookAndFeel.fontRegular());
            applet.noStroke();
            applet.text(item.getTitle(), itemSize + 10, height + itemSize * 0.5f);
        }
    }

    @Override
    public void setSize(float w, float h) {
        expandedHeight = h;
        expandedWidth = w;
        updateSize();
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        MenuItem item = getMenuItemForPosition(y);
        if (item == null) {
            return;
        }
        if (button == LEFT
                && items.indexOf(item) != 0
                && item != selectedMenuItem) {
            selectedMenuItem = item;
            invalidate();

        }
        item.triggerAction(button);
    }

    @Override
    public void onMouseMove(int x, int y) {
        MenuItem item = getMenuItemForPosition(y);
        if (item != mouseOverMenuItem) {
            mouseOverMenuItem = item;
            invalidate();
        }
    }

    private void updateSize() {
        float width = itemSize * 1.2f * (1 - collapseValue)
                + expandedWidth * collapseValue;
        super.setSize(width, expandedHeight);
        invalidateParent();
    }

    public void collapseAnimationFunction(float dt) {
        collapseValue = 1 - collapseAnimation.getProgressNormal();
        collapseValue = collapseValue * collapseValue;
        updateSize();
    }

    public void expandAnimationFunction(float dt) {
        float t = expandAnimation.getProgressNormal() - 1;
        collapseValue = -(t * t) + 1;
        updateSize();
    }

    private MenuItem getMenuItemForPosition(int y) {
        int topIndex = (int) (y / itemSize);
        int bottomIndex = (int) ((getHeight() - y - 10) / itemSize);
        if (topIndex < items.size()) {
            return items.get(topIndex);
        }
        if (bottomIndex < itemsBottom.size()) {
            return itemsBottom.get(bottomIndex);
        }
        return null;
    }

    @Override
    public void onMouseLeave() {
        mouseOverMenuItem = null;
        invalidate();
    }

    public void setCollapse(boolean state) {
        collapseValue = state ? 0 : 1;
        updateSize();
    }

    public void setCollapseAnimate(boolean collapsed) {
        if (collapsed) {
            collapseAnimation.restart();
        } else {
            expandAnimation.restart();
        }
    }

    public boolean isCollapsed() {
        return collapseValue < 0.5f;
    }

    public float getItemSize() {
        return itemSize;
    }

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public void addMenuItemBottom(MenuItem item) {
        itemsBottom.add(item);
    }

    public void removeMenuItem(MenuItem item) {
        items.remove(item);
        itemsBottom.remove(item);
    }

    public void setSelectedMenuItem(MenuItem item) {
        selectedMenuItem = item;
    }

    public MenuItem getSelectedItem() {
        return selectedMenuItem;
    }

    public static class MenuItem {

        /**
         * Title of the menu item.
         */
        private final String title;
        /**
         * Icon of the menu item.
         */
        private final PImage icon;
        /**
         * Click action. Consumes an integer that describes which button was
         * pressed.
         */
        private Consumer<Integer> action = (button) -> {
        };

        public MenuItem(String title, PImage icon) {
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void triggerAction(int button) {
            action.accept(button);
        }

        public PImage getIcon() {
            return icon;
        }

        public void setClickAction(Consumer<Integer> action) {
            this.action = action;
        }
    }

}
