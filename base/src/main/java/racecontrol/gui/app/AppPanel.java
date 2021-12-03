/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import racecontrol.gui.CustomPApplet;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.lpui.LPAnimationTask;
import racecontrol.gui.lpui.LPComponent;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.MENU_COLLAPSED;

/**
 *
 * @author Leonard
 */
public class AppPanel
        extends LPContainer {

    /**
     * Header shows the connection status and basic information.
     */
    protected final HeaderPanel header;
    /**
     * Menu to select the visible page.
     */
    protected final Menu menu;
    /**
     * Live timing menu item.
     */
    protected MenuItem liveTimingMenuItem;
    /**
     * Race control menu item.
     */
    protected MenuItem raceControlMenuItem;
    /**
     * Broadcast menu item.
     */
    protected MenuItem broadcastMenuItem;
    /**
     * Autopilot menu item.
     */
    protected MenuItem autopilotMenuItem;
    /**
     * Trackmap menu item.
     */
    protected MenuItem trackmapMenuItem;
    /**
     * Log menu item.
     */
    protected MenuItem logMenuItem;
    /**
     * Debug menu item.
     */
    protected MenuItem debugMenuItem;
    /**
     * Debug menu for track data.
     */
    protected MenuItem trackDataItem;
    /**
     * Settings menu item.
     */
    protected MenuItem settingsMenuItem;
    /**
     * Currently active page.
     */
    private LPComponent activePage;
    /**
     * Currently showing status panels.
     */
    private final List<StatusPanel> statusPanels = new LinkedList<>();
    /**
     * Time for the status panel animation.
     */
    private final int STATUS_PANEL_ANIMATION_TIME = 200;

    public AppPanel() {
        header = new HeaderPanel();
        addComponent(header);

        StatusPanel nullPanel = new StatusPanel(null);
        nullPanel.animationTask = new LPAnimationTask(nullPanel::animationFunction, STATUS_PANEL_ANIMATION_TIME);
        statusPanels.add(nullPanel);
        addAnimationTask(nullPanel.animationTask);

        menu = new Menu();
        addComponent(menu);
        MenuItem menuItem = new MenuItem("Menu", ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Symbol.png"));
        menuItem.setClickAction((button) -> {
            menu.setCollapseAnimate(!menu.isCollapsed());
            PersistantConfig.put(MENU_COLLAPSED, menu.isCollapsed());
        });
        menu.addMenuItem(menuItem);
        menu.setCollapse(PersistantConfig.get(MENU_COLLAPSED));
        updateComponents();
    }

    @Override
    public void onResize(float w, float h) {
        menu.setSize(200, h);
        updateComponents();
    }

    @Override
    public void draw(PApplet applet) {
        updateComponents();
    }

    public final void updateComponents() {
        menu.setPosition(0, 0);

        float menuWidth = menu.isVisible() ? menu.getWidth() : 0;
        float headerHeight = 0;
        for (int i = 0; i < statusPanels.size() - 1; i++) {
            StatusPanel s = statusPanels.get(i);
            s.panel.setSize(getWidth() - menuWidth, LINE_HEIGHT);
            s.panel.setPosition(menuWidth, headerHeight + s.animationOffset);
            headerHeight += LINE_HEIGHT + s.animationOffset;
        }
        headerHeight += statusPanels.get(statusPanels.size() - 1).animationOffset;

        header.setSize(getWidth() - menuWidth, LINE_HEIGHT);
        header.setPosition(menuWidth, headerHeight);
        headerHeight += LINE_HEIGHT;

        if (activePage != null) {
            activePage.setSize(getWidth() - menuWidth, getHeight() - headerHeight);
            activePage.setPosition(menuWidth, headerHeight);
        }
    }

    public void setActivePage(LPComponent page) {
        if (activePage != null) {
            activePage.setVisible(false);
            activePage.setEnabled(false);
        }
        if (!getComponents().contains(page)) {
            addComponent(page);
        }
        activePage = page;
        activePage.setVisible(true);
        activePage.setEnabled(true);
    }

    public void addStatusPanel(LPComponent panel) {
        if (indexOf(panel) == -1) {
            StatusPanel sp = new StatusPanel(panel);
            sp.animationTask = new LPAnimationTask(sp::animationFunction, STATUS_PANEL_ANIMATION_TIME);
            sp.animationTask.restart();
            addAnimationTask(sp.animationTask);
            sp.animationOffsetStart = -LINE_HEIGHT;
            statusPanels.add(0, sp);
            addComponent(panel);
            updateComponents();
            invalidate();
        }
    }

    public void removeStatusPanel(LPComponent statusPanel) {
        int index = indexOf(statusPanel);

        if (index != -1) {
            StatusPanel removedSp = statusPanels.get(index);
            StatusPanel animatedSp = statusPanels.get(index + 1);
            animatedSp.animationOffsetStart = removedSp.animationOffset
                    + animatedSp.animationOffset + LINE_HEIGHT;
            animatedSp.animationTask.restart();

            statusPanels.remove(removedSp);
            removeAnimationTask(removedSp.animationTask);
            removeComponent(removedSp.panel);
            updateComponents();
            invalidate();
        }

    }

    private int indexOf(LPComponent panel) {
        if (panel == null) {
            return -1;
        }
        for (int i = 0; i < statusPanels.size(); i++) {
            if (statusPanels.get(i).panel == panel) {
                return i;
            }
        }

        return -1;
    }

    private class StatusPanel {

        private final LPComponent panel;
        private float animationOffset = 0;
        private float animationOffsetStart = 0;
        private LPAnimationTask animationTask;

        public StatusPanel(LPComponent panel) {
            this.panel = panel;
        }

        public void animationFunction(LPAnimationTask task, float dt) {
            float t = task.getProgressNormal();
            t = (t - 1) * (t - 1);
            animationOffset = animationOffsetStart * t;
            updateComponents();
        }
    }
}
