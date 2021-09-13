/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import java.util.LinkedList;
import java.util.List;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPComponent;
import racecontrol.lpgui.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class AppPanel
        extends LPContainer {

    /**
     * Header shows the connection status and basic information.
     */
    private final HeaderPanel header;
    /**
     * Menu to select the visible page.
     */
    protected final Menu menu;
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

        updateComponents();
    }

    @Override
    public void onResize(float w, float h) {
        updateComponents();
    }

    public final void updateComponents() {
        menu.setSize(200, getHeight());
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

    public HeaderPanel getHeader() {
        return header;
    }

    public void setActivePage(LPComponent page) {
        if (activePage != null) {
            removeComponent(activePage);
        }
        activePage = page;
        addComponent(page);
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
