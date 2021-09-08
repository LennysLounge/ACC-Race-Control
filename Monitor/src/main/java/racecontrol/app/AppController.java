/**
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

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
    
    private final SettingsPanel settingsPanel;

    public AppController() {
        EventBus.register(this);
        
        appPanel = new AppPanel();
        settingsPanel = new SettingsPanel();
        
        LPComponent TestPanel = new testPanel();

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Menu", () -> {
            appPanel.getMenu().toggleCollapse();
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Test", () -> {
            appPanel.setActivePage(TestPanel);
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
