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
     * True if the menu is collapsed.
     */
    private boolean collapsed = true;
    /**
     * Menu width when collapsed.
     */
    private final float itemSize = LINE_HEIGHT * 1.5f;
    /**
     * List of all MenuItems for this menu.
     */
    private final List<MenuItem> items = new ArrayList<>();
    /**
     * Width when expanded.
     */
    private float expandedWidth;
    /**
     * Height when expanded.
     */
    private float expandedHeight;
    /**
     * Item index of the mouse position.
     */
    private int mouseOverItemIndex = -1;
    /**
     * Currently selected index.
     */
    private int selectedIndex = -1;

    public Menu() {
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        int i = 0;
        for (MenuItem item : items) {
            if (i == mouseOverItemIndex) {
                applet.fill(COLOR_DARK_RED);
                applet.rect(0, itemSize * i, getWidth(), itemSize);
            }
            if (i == selectedIndex) {
                applet.fill(COLOR_RED);
                applet.rect(0, itemSize * i, itemSize * 0.1f, itemSize);
            }

            //draw icon
            if (item.getIcon() != null) {
                applet.tint((i == selectedIndex || i == mouseOverItemIndex) ? 255 : 100);
                item.getIcon().resize((int) (itemSize * 0.6f), (int) (itemSize * 0.6f));
                applet.image(item.getIcon(), itemSize * 0.3f, itemSize * (i + 0.2f));
            } else {
                applet.noFill();
                applet.stroke(COLOR_WHITE);
                applet.strokeWeight(3);
                applet.rect(itemSize * 0.3f, itemSize * (i + 0.2f), itemSize * 0.6f, itemSize * 0.6f);
                applet.strokeWeight(1);
                applet.noStroke();
            }

            if (!collapsed) {
                //draw text
                applet.textAlign(LEFT, CENTER);
                applet.fill((i == selectedIndex || i == mouseOverItemIndex) ? COLOR_WHITE : 100);
                applet.textFont(LookAndFeel.fontRegular());
                applet.noStroke();
                applet.text(item.getTitle(), itemSize + 10, itemSize * (i + 0.5f));
            }
            i++;
        }
    }

    @Override
    public void setSize(float w, float h) {
        expandedHeight = h;
        expandedWidth = w;
        if (collapsed) {
            super.setSize(itemSize * 1.2f, h);
        } else {
            super.setSize(w, h);
        }
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        int itemIndex = (int) (y / itemSize);
        if (itemIndex < items.size()) {
            int oldIndex = selectedIndex;
            selectedIndex = itemIndex;
            items.get(itemIndex).triggerAction(items.get(oldIndex));
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
        int itemIndex = (int) (y / itemSize);
        if (itemIndex < items.size()) {
            if (itemIndex != mouseOverItemIndex) {
                mouseOverItemIndex = itemIndex;
                invalidate();
            }
        }
    }

    @Override
    public void onMouseLeave() {
        mouseOverItemIndex = -1;
        invalidate();
    }

    public void expand() {
        collapsed = false;
        setSize(expandedWidth, expandedHeight);
    }

    public void collapse() {
        collapsed = true;
        setSize(expandedWidth, expandedHeight);
    }

    public void toggleCollapse() {
        collapsed = !collapsed;
        setSize(expandedWidth, expandedHeight);
    }

    public float getItemSize() {
        return itemSize;
    }

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public void removeMenuItem(MenuItem item) {
        items.remove(item);
    }

    public void setSelectedMenuItem(MenuItem item) {
        selectedIndex = items.indexOf(item);
    }

    public void setSelectedMenuIndex(int index) {
        selectedIndex = index;
    }

    public static class MenuItem {

        /**
         * Title of the menu item.
         */
        private final String title;
        /**
         * Icon of the menu item.
         */
        private PImage icon;
        /**
         * Click action.
         */
        private final Consumer<MenuItem> action;

        public MenuItem(String title,
                PImage icon,
                Runnable action) {
            this(title, icon, (MenuItem item) -> action.run());
        }

        public MenuItem(String title,
                PImage icon,
                Consumer<MenuItem> action) {
            this.title = title;
            this.icon = icon;
            this.action = action;
        }

        public String getTitle() {
            return title;
        }

        public void triggerAction(MenuItem prev) {
            action.accept(prev);
        }

        public PImage getIcon() {
            return icon;
        }
    }

}
