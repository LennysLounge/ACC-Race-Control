/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.debug;

import racecontrol.client.AccBroadcastingClient;
import racecontrol.eventbus.EventBus;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.eventbus.Event;
import racecontrol.extensions.replayoffset.ReplayStart;
import racecontrol.lpgui.gui.LPContainer;

/**
 * A Basic Extension to test stuff out with.
 *
 * @author Leonard
 */
public class DebugExtension
        extends AccClientExtension {

    DebugPanel panel;

    public DebugExtension(AccBroadcastingClient client) {
        super(client);
        this.panel = new DebugPanel();
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }
    
    @Override
    public void onEvent(Event e) {
        if (e instanceof ReplayStart) {
            panel.setReplayTimeKnown();
        }
    }
}
