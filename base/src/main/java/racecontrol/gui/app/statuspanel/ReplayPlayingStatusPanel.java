/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.statuspanel;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_BLUE;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class ReplayPlayingStatusPanel
        extends LPContainer
        implements EventListener {

    private final AccBroadcastingClient client;

    public ReplayPlayingStatusPanel() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_BLUE);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text("Replay time remaining: " + TimeUtils.asDuration(client.getModel().session.raw.getReplayRemainingTime()),
                10, LINE_HEIGHT * 0.5f);

        applet.text("Session Time: " + TimeUtils.asDuration(client.getModel().session.raw.getReplaySessionTime()),
                360, LINE_HEIGHT * 0.5f);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof AfterPacketReceivedEvent) {
            invalidate();
        }
    }

}
