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
    private final LPTabPanel body;
    private final ConfigPanel configPanel;

    private final AccBroadcastingClient client;

    public BasePanel(AccBroadcastingClient client) {
        this.client = client;
        EventBus.register(this);
        header = new HeaderPanel(client);
        addComponent(header);

        body = new LPTabPanel();
        addComponent(body);

        configPanel = new ConfigPanel(client);
        body.addTab(configPanel);
        body.setTabIndex(0);
    }

    public void updateHeader() {
        header.invalidate();
    }

    @Override
    public void onResize(int w, int h) {
        int headerSize = LookAndFeel.LINE_HEIGHT;
        header.setSize(w, headerSize);
        header.setPosition(0, 0);

        body.setSize(w, h - headerSize);
        body.setPosition(0, headerSize);
        invalidate();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpened) {
            client.getExtensionPanels().stream()
                    .forEach(panel -> body.addTab(panel));

            body.setTabIndex(1);
            invalidate();
        } else if (e instanceof ConnectionClosed) {
            body.removeAllTabs();
            body.addTab(configPanel);
            body.setTabIndex(0);
            invalidate();
        }
    }

}
