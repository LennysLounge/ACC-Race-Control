/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.debug;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.replayoffset.ReplayStart;
import base.screen.visualisation.gui.LPContainer;

/**
 * A Basic Extension to test stuff out with.
 *
 * @author Leonard
 */
public class DebugExtension
        implements AccClientExtension, EventListener {

    DebugPanel panel;

    public DebugExtension() {
        this.panel = new DebugPanel();
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void removeExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ReplayStart) {
            panel.setReplayTimeKnown();
        }
    }

}
