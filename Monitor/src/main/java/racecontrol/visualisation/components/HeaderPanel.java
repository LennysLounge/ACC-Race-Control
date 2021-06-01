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
import racecontrol.visualisation.gui.LPComponent;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class HeaderPanel extends LPComponent {

    private final AccBroadcastingClient client;

    private final ReplayOffsetExtension replayOffsetExtension;

    public HeaderPanel(AccBroadcastingClient client) {
        this.client = client;
        replayOffsetExtension = client.getOrCreateExtension(ReplayOffsetExtension.class);
    }

    @Override
    public void draw() {
        if (client.isConnected()) {
            applet.fill(COLOR_DARK_DARK_GRAY);
            if (client.getModel().getSessionInfo().isReplayPlaying()) {
                applet.fill(COLOR_BLUE);
            }
            if (replayOffsetExtension.isSearching()) {
                applet.fill(LookAndFeel.COLOR_GREEN);
            }

            applet.noStroke();
            applet.rect(0, 0, getWidth(), getHeight());
            int y = 0;

            String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
            String sessionName = sessionIdToString(client.getSessionId());
            String conId = "CON-ID: " + client.getModel().getConnectionID();
            String packetsReceived = "Packets received: " + client.getPacketCount();
            applet.fill(255);
            applet.textAlign(LEFT, CENTER);
            applet.textFont(fontRegular());
            applet.text(conId, 10, y + LINE_HEIGHT * 0.5f);
            applet.text(packetsReceived, 200, y + LINE_HEIGHT * 0.5f);
            if (client.getModel().getSessionInfo().isReplayPlaying()) {
                if (!replayOffsetExtension.isSearching()) {
                    applet.text("Replay time remaining: " + TimeUtils.asDuration(client.getModel().getSessionInfo().getReplayRemainingTime()),
                            500, LINE_HEIGHT * 0.5f);

                    applet.text("Session Time: " + TimeUtils.asDuration(client.getModel().getSessionInfo().getReplaySessionTime()),
                            850, LINE_HEIGHT * 0.5f);
                }
            }
            if (replayOffsetExtension.isSearching()) {
                applet.text("Searching for replay time, please wait", 500, LINE_HEIGHT * 0.5f);
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

}
