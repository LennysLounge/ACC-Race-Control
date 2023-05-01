/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.autobroadcast;

import processing.core.PApplet;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.extension.autobroadcast.AutobroadcastExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.app.statuspanel.StatusPanelManager;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 *
 * @author Leonard
 */
public class AutobroadcastEnableStatusPanel
        extends LPContainer
        implements EventListener {

    protected final LPButton closeButton = new LPButton("Disable");
    private final LPLabel message = new LPLabel("Auto cam enabled");
    private final int backgroundColor = LookAndFeel.COLOR_ORANGE;

    public AutobroadcastEnableStatusPanel() {
        EventBus.register(this);
        message.setPosition(20, 0);
        addComponent(message);
        message.setBackground(backgroundColor);

        closeButton.setSize(100, LINE_HEIGHT);
        closeButton.setPosition(400, 0);
        closeButton.setAction(this::disableAutobroadcast);
        addComponent(closeButton);
    }

    @Override
    public void onResize(float w, float h) {
        closeButton.setPosition(w - 120, 0);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(backgroundColor);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof AutobroadcastDisabledEvent) {
            StatusPanelManager.getInstance().removeStatusPanel(this);
        }
    }

    private void disableAutobroadcast() {
        AutobroadcastExtension.getInstance().setEnabled(false);
    }

}
