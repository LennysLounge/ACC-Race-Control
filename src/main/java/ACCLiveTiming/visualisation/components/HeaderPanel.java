/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.components;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.utility.SpreadSheetService;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPComponent;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class HeaderPanel extends LPComponent {

    private final BasicAccBroadcastingClient client;

    private boolean showSpreadSheetStatus;

    public HeaderPanel(BasicAccBroadcastingClient client, boolean showSpreadSheetStatus) {
        this.client = client;
        this.showSpreadSheetStatus = showSpreadSheetStatus;
    }

    @Override
    public void draw() {
        applet.fill(30);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());
        int y = 0;
        //Draw Spreasheet status.
        if (showSpreadSheetStatus) {
            applet.fill(LookAndFeel.get().COLOR_RACE);
            applet.rect(0, 0, getWidth(), LookAndFeel.get().LINE_HEIGHT);
            applet.fill(30);

            String targetSheet = SpreadSheetService.getSheet();
            applet.textAlign(LEFT, CENTER);
            applet.text("Target Sheet:\"" + targetSheet + "\"", 10, LookAndFeel.get().LINE_HEIGHT * 0.5f);
            y += LookAndFeel.get().LINE_HEIGHT;
        }

        String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
        String sessionName = sessionIdToString(client.getSessionId());
        String conId = "CON-ID: " + client.getModel().getConnectionID();
        String packetsReceived = "Packets received: " + client.getPacketCount();
        applet.fill(255);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.get().FONT);
        applet.text(conId, 10, y + LookAndFeel.get().LINE_HEIGHT * 0.5f);
        applet.text(packetsReceived, 200, y + LookAndFeel.get().LINE_HEIGHT * 0.5f);
        applet.textAlign(RIGHT, CENTER);
        applet.text(sessionName, applet.width - 10, y + LookAndFeel.get().LINE_HEIGHT * 0.5f);
        applet.text(sessionTimeLeft,
                applet.width - applet.textWidth(sessionName) - 40,
                y + LookAndFeel.get().LINE_HEIGHT / 2f);

        applet.fill(LookAndFeel.get().COLOR_RACE);
        applet.rect(applet.width - applet.textWidth(sessionName) - 30,
                y + LookAndFeel.get().LINE_HEIGHT * 0.1f,
                10, LookAndFeel.get().LINE_HEIGHT * 0.8f);

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
