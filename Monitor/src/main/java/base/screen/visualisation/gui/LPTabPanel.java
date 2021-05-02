/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.visualisation.gui;

import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.COLOR_RED;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.HAND;

/**
 *
 * @author Leonard
 */
public class LPTabPanel extends LPContainer {

    /**
     * List of tabs.
     */
    private List<LPContainer> tabs = new LinkedList<>();
    /**
     * The index of the currently displayed panel.
     */
    private int tabIndex = 0;
    /**
     * The currently displayed tab.
     */
    private LPComponent currentTab = null;
    /**
     * The tab index the mouse is over.
     */
    private int mouseOverTab = -1;
    /**
     * Color for the active tab.
     */
    private int activeTabColor = COLOR_DARK_GRAY;

    public LPTabPanel() {
    }

    public void addTab(LPContainer tab) {
        tabs.add(tab);
        setTabIndex(tabs.size() - 1);
    }

    public void removeTab(LPContainer tab) {
        tabs.remove(tab);
    }

    public void removeAllTabs() {
        tabs.clear();
    }

    public List<LPContainer> getTabs() {
        return tabs;
    }

    public void setActiveTabColor(int color) {
        this.activeTabColor = color;
    }

    @Override
    public void draw() {
        if (tabs.isEmpty()) {
            return;
        }

        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        int lineHeight = LookAndFeel.LINE_HEIGHT;

        applet.textAlign(CENTER, CENTER);
        applet.fill(LookAndFeel.COLOR_DARK_DARK_GRAY);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), lineHeight);
        if (tabs.size() > 1) {
            float tabSize = getWidth() / tabs.size();
            for (int i = 0; i < tabs.size(); i++) {
                if (i == tabIndex) {
                    applet.fill(activeTabColor);
                    applet.rect(i * tabSize, 0, tabSize, lineHeight);
                }
                if (i == mouseOverTab) {
                    applet.fill(COLOR_RED);
                    applet.rect(i * tabSize, 0, tabSize, lineHeight);
                }
                applet.fill(255);
                applet.textFont(LookAndFeel.fontMedium());
                applet.text(tabs.get(i).getName(), i * tabSize + tabSize / 2f, lineHeight * 0.5f);
            }
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (tabs.size() <= 1) {
            setMouseOverTab(-1);
            return;
        }
        if (y > 0 && y < LookAndFeel.LINE_HEIGHT) {
            float tabSize = getWidth() / tabs.size();
            int index = (int) ((x - (x % tabSize)) / tabSize);
            setMouseOverTab(index);
        } else {
            setMouseOverTab(-1);
        }
    }

    @Override
    public void onMouseLeave() {
        setMouseOverTab(-1);
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
        if (tabs.size() <= 1) {
            return;
        }
        int lineHeight = LookAndFeel.LINE_HEIGHT;
        if (y > 0 && y < lineHeight) {
            float tabSize = getWidth() / tabs.size();
            int clickedIndex = (int) ((x - (x % tabSize)) / tabSize);
            setTabIndex(clickedIndex);
        }
    }

    public void setTabIndex(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        tabIndex = index;
        if (currentTab != null) {
            removeComponent(currentTab);
        }
        currentTab = tabs.get(tabIndex);
        resetTabSize();
        addComponent(currentTab);
        invalidate();
    }

    @Override
    public void onResize(int w, int h) {
        resetTabSize();
    }

    private void resetTabSize() {
        if (currentTab != null) {
            if (tabs.size() > 1) {
                currentTab.setPosition(0, LookAndFeel.LINE_HEIGHT);
                currentTab.setSize(getWidth(), getHeight() - LookAndFeel.LINE_HEIGHT);
            } else {
                currentTab.setPosition(0, 0);
                currentTab.setSize(getWidth(), getHeight());
            }
        }
    }

    private void setMouseOverTab(int index) {
        if (index != mouseOverTab) {
            invalidate();
            if (index == -1) {
                applet.cursor(ARROW);
            } else {
                applet.cursor(HAND);
            }
        }
        mouseOverTab = index;
    }

    public int getTabCount() {
        return tabs.size();
    }

}
