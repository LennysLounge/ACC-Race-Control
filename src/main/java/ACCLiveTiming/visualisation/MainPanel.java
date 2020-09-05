/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.extensions.GraphicsFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import processing.core.PApplet;

/**
 *
 * @author Leonard
 */
public class MainPanel extends ExtensionPanel {

    private final PApplet visualisation;
    private final BasicAccBroadcastingClient client;
    /**
     * List of the extension panels.
     */
    private List<ExtensionPanel> panels = new LinkedList<>();
    /**
     * List with the tab names.
     */
    private List<String> tabNames = new LinkedList<>();

    private int activeTabIndex = 0;

    public MainPanel(PApplet visualisation,
            BasicAccBroadcastingClient client) {
        this.visualisation = visualisation;
        this.client = client;
        this.panels = client.getPanels();
        tabNames = panels.stream()
                .map(panel -> panel.getDisplayName())
                .collect(Collectors.toList());
    }

    @Override
    public void drawPanel() {

        /*
        String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
        String sessionName = sessionIdToString(client.getSessionId());
        layer.textAlign(LEFT, CENTER);
        layer.fill(30);
        layer.noStroke();
        layer.textFont(LookAndFeel.get().FONT);

        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        //First line
        layer.rect(0, 0, layer.width, lineHeight);
        String conId = "CON-ID: " + client.getModel().getConnectionID();
        String packetsReceived = "Packets received: " + client.getPacketCount();
        String fr = "FrameRate:" + visualisation.frameRate;

        layer.fill(255);
        layer.text(conId, 10, lineHeight / 2f);
        layer.text(packetsReceived, 200, lineHeight / 2f);
        layer.text(fr, 400, lineHeight / 2f);

        layer.textAlign(RIGHT, CENTER);
        layer.text(sessionName, layer.width - 10, lineHeight / 2f);
        layer.text(sessionTimeLeft, layer.width - layer.textWidth(sessionName) - 40, lineHeight / 2f);

        layer.fill(0, 150, 0);
        layer.rect(layer.width - layer.textWidth(sessionName) - 30, lineHeight * 0.1f, 10, lineHeight * 0.8f);

        //tabs
        layer.textAlign(CENTER, CENTER);
        float tabSize = layer.width / tabNames.size();
        for (int i = 0; i < tabNames.size(); i++) {
            layer.fill((i == activeTabIndex) ? 30 : 50);
            layer.rect(i * tabSize, lineHeight, tabSize, lineHeight);

            layer.fill(255);
            layer.text(tabNames.get(i), i * tabSize + tabSize / 2f, lineHeight * 1.5f);
        }
         */
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
        return result + " " + sessionId.getNumber();
    }

    public void setActiveTabIndex(int index) {
        this.activeTabIndex = index;
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }
    
    @Override
    public void resize(GraphicsFactory factory, int w, int h) {
        layer = factory.createGraphics(w, h);
        
        //resize panels
    }

}
