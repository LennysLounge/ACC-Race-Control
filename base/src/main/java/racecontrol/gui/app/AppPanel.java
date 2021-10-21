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
    protected final MenuItem liveTimingMenuItem;
    /**
     * Race control menu item.
     */
    protected final MenuItem raceControlMenuItem;
    /**
     * Broadcast menu item.
     */
    protected final MenuItem broadcastMenuItem;
    /**
     * Autopilot menu item.
     */
    protected final MenuItem autopilotMenuItem;
    /**
     * Trackmap menu item.
     */
    protected final MenuItem trackmapMenuItem;
    /**
     * Log menu item.
     */
    protected final MenuItem logMenuItem;
    /**
     * Debug menu item.
     */
    protected final MenuItem debugMenuItem;
    /**
     * Debug menu for track data.
     */
    protected final MenuItem trackDataItem;
    /**
     * Settings menu item.
     */
    protected final MenuItem settingsMenuItem;
    /**
     * Currently active page.
     */
    private LPComponent activePage;
    /**
     * Currently showing status panels.
     */
    private final List<LPComponent> statusPanels = new LinkedList<>();

    public AppPanel() {
        header = new HeaderPanel();
        addComponent(header);

        menu = new Menu();
        addComponent(menu);
        MenuItem menuItem = new MenuItem("Menu", ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Symbol.png"));
        menuItem.setClickAction((button) -> {
            menu.setCollapseAnimate(!menu.isCollapsed());
            PersistantConfig.put(MENU_COLLAPSED, menu.isCollapsed());
        });
        menu.addMenuItem(menuItem);

        liveTimingMenuItem = new MenuItem("Live Timing",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_LiveTiming.png"));
        menu.addMenuItem(liveTimingMenuItem);

        raceControlMenuItem = new MenuItem("Race Control",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Control.png"));
        menu.addMenuItem(raceControlMenuItem);

        broadcastMenuItem = new MenuItem("Broadcast",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Broadcast.png"));
        //menu.addMenuItem(broadcastMenuItem);

        autopilotMenuItem = new MenuItem("Autopilot",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_AutoBroadcast.png"));
        //menu.addMenuItem(autopilotMenuItem);

        trackmapMenuItem = new MenuItem("Trackmap",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_TrackMap.png"));
        //menu.addMenuItem(trackmapMenuItem);

        logMenuItem = new MenuItem("Log",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_LOG.png"));
        menu.addMenuItem(logMenuItem);

        debugMenuItem = new MenuItem("Debug",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Debugging.png"));
        menu.addMenuItem(debugMenuItem);

        trackDataItem = new MenuItem("Track Data",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Debugging.png"));
        //menu.addMenuItem(trackDataItem);

        settingsMenuItem = new MenuItem("Settings",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Settings.png"));
        menu.addMenuItemBottom(settingsMenuItem);

        menu.setCollapse(PersistantConfig.get(MENU_COLLAPSED));
        
        updateComponents();
    }

    @Override
    public void onResize(float w, float h) {
        menu.setSize(200, h);
        updateComponents();
    }
    
    @Override
    public void draw(PApplet applet){
        updateComponents();
    }

    public final void updateComponents() {
        menu.setPosition(0, 0);

        float menuWidth = menu.isVisible() ? menu.getWidth() : 0;
        float headerHeight = 0;
        for (LPComponent statusPanel : statusPanels) {
            statusPanel.setSize(getWidth() - menuWidth, LINE_HEIGHT);
            statusPanel.setPosition(menuWidth, headerHeight);
            headerHeight += LINE_HEIGHT;
        }
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

    public void addStatusPanel(LPComponent statusPanel) {
        if (!statusPanels.contains(statusPanel)) {
            statusPanels.add(statusPanel);
            addComponent(statusPanel);
            updateComponents();
            invalidate();
        }
    }

    public void removeStatusPanel(LPComponent statusPanel) {
        statusPanels.remove(statusPanel);
        removeComponent(statusPanel);
        updateComponents();
        invalidate();
    }
}
