/**
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import racecontrol.app.logging.LoggingPanel;
import racecontrol.client.events.ConnectionClosed;
import racecontrol.client.events.ConnectionOpened;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.lpgui.gui.LPComponent;

/**
 *
 * @author Leonard
 */
public class AppController 
    implements EventListener{

    /**
     * The GUI component.
     */
    private final AppPanel appPanel;
    /**
     * Settings panel.
     */
    private final SettingsPanel settingsPanel;
    /**
     * Logging panel.
     */
    private final LoggingPanel loggingPanel;

    public AppController() {
        EventBus.register(this);
        
        appPanel = new AppPanel();
        settingsPanel = new SettingsPanel();
        loggingPanel = new LoggingPanel();
        
        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Menu", () -> {
            appPanel.getMenu().toggleCollapse();
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Log", () -> {
            appPanel.setActivePage(loggingPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        Menu.MenuItem settingsMenuItem = new Menu.MenuItem("Settings", () -> {
            appPanel.setActivePage(settingsPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        });
        appPanel.getMenu().addMenuItem(settingsMenuItem);
        appPanel.getMenu().setVisible(false);
        
        appPanel.setActivePage(settingsPanel);
        appPanel.getMenu().setSelectedMenuItem(settingsMenuItem);
    }

    public LPComponent getGUIComponent() {
        return appPanel;
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof RealtimeUpdate){
            appPanel.getHeader().invalidate();
        }
        else if(e instanceof ConnectionOpened){
            appPanel.getMenu().setVisible(true);
            appPanel.updateComponents();
            appPanel.invalidate();
        }
        else if(e instanceof ConnectionClosed){
            appPanel.getMenu().setVisible(false);
            appPanel.setActivePage(settingsPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        }
    }

}
