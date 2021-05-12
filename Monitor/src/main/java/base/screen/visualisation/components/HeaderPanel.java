/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.visualisation.components;

import base.screen.networking.SessionId;
import base.screen.networking.AccBroadcastingClient;
import base.screen.utility.TimeUtils;
import static base.screen.visualisation.LookAndFeel.COLOR_BLUE;
import static base.screen.visualisation.LookAndFeel.COLOR_DARK_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.COLOR_RED;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import static base.screen.visualisation.LookAndFeel.TEXT_SIZE;
import static base.screen.visualisation.LookAndFeel.fontMedium;
import static base.screen.visualisation.LookAndFeel.fontRegular;
import base.screen.visualisation.gui.LPComponent;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class HeaderPanel extends LPComponent {

    private final AccBroadcastingClient client;

    public HeaderPanel(AccBroadcastingClient client) {
        this.client = client;
    }

    @Override
    public void draw() {
        if (client.isConnected()) {
            applet.fill(COLOR_DARK_DARK_GRAY);
            if(client.getModel().getSessionInfo().isReplayPlaying()){
                applet.fill(COLOR_BLUE);
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
            if(client.getModel().getSessionInfo().isReplayPlaying()){
                applet.text("Replay time remaining: " + TimeUtils.asDuration(client.getModel().getSessionInfo().getReplayRemainingTime()),
                        500, LINE_HEIGHT*0.5f);
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
                    7, LINE_HEIGHT * 0.8f);
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
