/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation.components;

import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.ConnectionClosed;
import racecontrol.client.events.ConnectionOpened;
import racecontrol.visualisation.LookAndFeel;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class BasePanel
        extends LPContainer
        implements EventListener {

    private final HeaderPanel header;
    private final LPTabPanel tabs;
    private final SettingsPanel settingsPanel;

    private final AccBroadcastingClient client;
    
    private boolean showSettings = true;

    public BasePanel(AccBroadcastingClient client) {
        this.client = client;
        EventBus.register(this);
        header = new HeaderPanel(client, this);
        addComponent(header);

        tabs = new LPTabPanel();
        addComponent(tabs);

        settingsPanel = new SettingsPanel(client);
        addComponent(settingsPanel);
        updateComponents();
    }
    
    public void updateComponents(){
        tabs.setVisible(!showSettings);
        settingsPanel.setVisible(showSettings);
    }

    public void updateHeader() {
        header.invalidate();
    }

    @Override
    public void onResize(int w, int h) {
        int headerSize = LookAndFeel.LINE_HEIGHT;
        header.setSize(w, headerSize);
        header.setPosition(0, 0);

        tabs.setSize(w, h - headerSize);
        tabs.setPosition(0, headerSize);
        
        settingsPanel.setSize(w, h - headerSize);
        settingsPanel.setPosition(0, headerSize);
        invalidate();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpened) {
            client.getExtensionPanels().stream()
                    .forEach(panel -> tabs.addTab(panel));

            tabs.setTabIndex(1);
            showSettings = false;
            updateComponents();
            invalidate();
        } else if (e instanceof ConnectionClosed) {
            tabs.removeAllTabs();
            showSettings = true;
            updateComponents();
            invalidate();
        }
    }
    
    public void toggleSettings(){
        showSettings = !showSettings;
        updateComponents();
    }

}
