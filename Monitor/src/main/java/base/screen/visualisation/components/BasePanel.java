/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.visualisation.components;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.networking.AccBroadcastingClient;
import base.screen.networking.events.ConnectionClosed;
import base.screen.networking.events.ConnectionOpened;
import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.Visualisation;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPTabPanel;
import java.util.List;
import java.util.stream.Collectors;

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

    public BasePanel(AccBroadcastingClient client) {
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
            Visualisation.getModules().stream()
                    .map(module -> module.getExtension())
                    .filter(extension -> extension != null)
                    .map(extension -> extension.getPanel())
                    .filter(panel -> panel != null)
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
