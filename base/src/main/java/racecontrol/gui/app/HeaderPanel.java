/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import processing.core.PApplet;
import racecontrol.client.data.SessionId;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.utility.TimeUtils;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_RED;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import static racecontrol.gui.LookAndFeel.fontMedium;
import static racecontrol.gui.LookAndFeel.fontRegular;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class HeaderPanel
        extends LPContainer {

    /**
     * Reference to the game client;
     */
    private final AccBroadcastingClient client;
    /**
     * Reference to the google sheets extension.
     */
    private final GoogleSheetsAPIExtension googleSheetController;

    public HeaderPanel() {
        this.client = AccBroadcastingClient.getClient();
        this.googleSheetController = GoogleSheetsAPIExtension.getInstance();
    }

    @Override
    public void draw(PApplet applet) {
        if (client.isConnected()) {
            applet.fill(COLOR_DARK_DARK_GRAY);
            applet.noStroke();
            applet.rect(0, 0, getWidth(), getHeight());

            applet.fill(255);
            applet.textAlign(LEFT, CENTER);
            applet.textFont(fontRegular());
            String conId = "Connection ID: " + client.getModel().getConnectionID();
            applet.text(conId, 10, LINE_HEIGHT * 0.5f);

            if (googleSheetController.isRunning()) {
                String googleSheetsActive = "Connected to:  \""
                        + googleSheetController.getSpreadsheetTitle()
                        + "::" + googleSheetController.getCurrentTargetSheet()
                        + "\"";
                applet.text(googleSheetsActive, 200, LINE_HEIGHT * 0.5f);
            }

            applet.textAlign(RIGHT, CENTER);
            applet.textSize(TEXT_SIZE * 0.8f);
            String sessionName = sessionIdToString(client.getSessionId());
            float sessionNameWidth = applet.textWidth(sessionName);
            applet.text(sessionName, getWidth() - 10, LINE_HEIGHT * 0.5f);

            applet.textFont(fontMedium());
            applet.textSize(TEXT_SIZE);
            String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
            applet.text(sessionTimeLeft,
                    getWidth() - sessionNameWidth - 27,
                    LINE_HEIGHT * 0.5f);

            applet.fill(0xff359425);
            applet.rect(getWidth() - sessionNameWidth - 22,
                    LINE_HEIGHT * 0.1f,
                    LINE_HEIGHT * 0.175f, LINE_HEIGHT * 0.8f);
        } else {
            applet.fill(COLOR_RED);
            applet.noStroke();
            applet.rect(0, 0, getWidth(), getHeight());
            applet.textAlign(LEFT, CENTER);
            applet.textFont(fontMedium());
            applet.fill(COLOR_WHITE);
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
