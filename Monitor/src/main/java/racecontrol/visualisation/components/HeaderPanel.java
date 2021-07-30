/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation.components;

import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.client.data.SessionId;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.utility.TimeUtils;
import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_BLUE;
import static racecontrol.visualisation.LookAndFeel.COLOR_DARK_DARK_GRAY;
import static racecontrol.visualisation.LookAndFeel.COLOR_RED;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import static racecontrol.visualisation.LookAndFeel.TEXT_SIZE;
import static racecontrol.visualisation.LookAndFeel.fontMedium;
import static racecontrol.visualisation.LookAndFeel.fontRegular;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import racecontrol.client.events.ConnectionClosed;
import racecontrol.client.events.ConnectionOpened;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.visualisation.gui.LPButton;
import racecontrol.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class HeaderPanel
        extends LPContainer
        implements EventListener {

    private final AccBroadcastingClient client;
    /**
     * Reference to the replay offset extension.
     */
    private ReplayOffsetExtension replayOffsetExtension;
    /**
     * Button for opening the settings.
     */
    private final LPButton settingsButton = new LPButton("Settings");
    /**
     * Background color for the button.
     */
    private int buttonColor = COLOR_DARK_DARK_GRAY;
    /**
     * Tracks the visibility of the setting panel in BasePanel.
     */
    private boolean settingsVisible = false;

    public HeaderPanel(AccBroadcastingClient client, BasePanel basePanel) {
        this.client = client;
        EventBus.register(this);

        settingsButton.setPosition((int) (LINE_HEIGHT * 0.1f), (int) (LINE_HEIGHT * 0.05f));
        settingsButton.setSize(TEXT_SIZE * 6, (int) (LINE_HEIGHT * 0.9f));
        settingsButton.setVisible(false);
        settingsButton.setAction(() -> {
            basePanel.toggleSettings();
            settingsVisible = !settingsVisible;
            settingsButton.setText(settingsVisible ? "Close" : "Settings");
        });
        settingsButton.setBackgroundColor(buttonColor);
        addComponent(settingsButton);
    }

    @Override
    public void draw() {
        if (client.isConnected()) {
            int backgroundColor = COLOR_DARK_DARK_GRAY;
            if (client.getModel().getSessionInfo().isReplayPlaying()) {
                backgroundColor = COLOR_BLUE;
            }
            if (replayOffsetExtension != null) {
                if (replayOffsetExtension.isSearching()) {
                    backgroundColor = LookAndFeel.COLOR_GREEN;
                }
            }
            applet.fill(backgroundColor);
            if (buttonColor != backgroundColor) {
                buttonColor = backgroundColor;
                settingsButton.setBackgroundColor(buttonColor);
            }

            applet.noStroke();
            applet.rect(0, 0, getWidth(), getHeight());
            int y = 0;

            String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
            String sessionName = sessionIdToString(client.getSessionId());
            String packetsReceived = "Packets received: " + client.getPacketCount();
            applet.fill(255);
            applet.textAlign(LEFT, CENTER);
            applet.textFont(fontRegular());
            applet.text(packetsReceived, 140, y + LINE_HEIGHT * 0.5f);
            if (client.getModel().getSessionInfo().isReplayPlaying()) {
                if (!replayOffsetExtension.isSearching()) {
                    applet.text("Replay time remaining: " + TimeUtils.asDuration(client.getModel().getSessionInfo().getReplayRemainingTime()),
                            500, LINE_HEIGHT * 0.5f);

                    applet.text("Session Time: " + TimeUtils.asDuration(client.getModel().getSessionInfo().getReplaySessionTime()),
                            850, LINE_HEIGHT * 0.5f);
                }
            }
            if (replayOffsetExtension != null) {
                if (replayOffsetExtension.isSearching()) {
                    applet.text("Searching for replay time, please wait", 500, LINE_HEIGHT * 0.5f);
                }
            }

            applet.textAlign(RIGHT, CENTER);
            applet.textSize(TEXT_SIZE * 0.8f);
            float sessionNameWidth = applet.textWidth(sessionName);
            applet.text(sessionName, applet.width - 10, y + LINE_HEIGHT * 0.5f);
            applet.textFont(fontMedium());
            applet.textSize(TEXT_SIZE);
            applet.text(sessionTimeLeft,
                    applet.width - sessionNameWidth - 27,
                    y + LINE_HEIGHT / 2f);

            applet.fill(0xff359425);
            applet.rect(applet.width - sessionNameWidth - 22,
                    y + LINE_HEIGHT * 0.1f,
                    LINE_HEIGHT * 0.175f, LINE_HEIGHT * 0.8f);
        } else {
            applet.fill(COLOR_RED);
            applet.noStroke();
            applet.rect(0, 0, getWidth(), getHeight());
            applet.textAlign(LEFT, CENTER);
            applet.textFont(fontMedium());
            applet.fill(COLOR_DARK_DARK_GRAY);
            applet.text("Not Connected", 10, LINE_HEIGHT * 0.5f);
        }
    }

    private String sessionIdToString(SessionId sessionId) {
        String result = "";
        switch (sessionId.getType()) {
            case PRACTICE:
                result = "PRACTICE";
                break;
            case QUALIFYING:
                result = "QUALIFYING";
                break;
            case RACE:
                result = "RACE";
                break;
            default:
                result = "NOT SUPPORTED";
                break;
        }
        return result;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpened) {
            settingsButton.setVisible(true);
            replayOffsetExtension = client.getOrCreateExtension(ReplayOffsetExtension.class);
        } else if (e instanceof ConnectionClosed) {
            settingsButton.setVisible(false);
            settingsVisible = false;
            settingsButton.setText(settingsVisible ? "Close" : "Settings");
        }
    }
}
