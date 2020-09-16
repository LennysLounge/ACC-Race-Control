/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.utility.TimeUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

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
        String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
        String sessionName = sessionIdToString(client.getSessionId());
        applet.textAlign(LEFT, CENTER);
        applet.fill(30);
        applet.noStroke();
        applet.textFont(LookAndFeel.get().FONT);

        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        //First line
        applet.rect(0, 0, applet.width, lineHeight);
        String conId = "CON-ID: " + client.getModel().getConnectionID();
        String packetsReceived = "Packets received: " + client.getPacketCount();
        String fr = "FrameRate:" + visualisation.frameRate;

        applet.fill(255);
        applet.text(conId, 10, lineHeight / 2f);
        applet.text(packetsReceived, 200, lineHeight / 2f);
        applet.text(fr, 400, lineHeight / 2f);

        applet.textAlign(RIGHT, CENTER);
        applet.text(sessionName, applet.width - 10, lineHeight / 2f);
        applet.text(sessionTimeLeft, applet.width - applet.textWidth(sessionName) - 40, lineHeight / 2f);

        applet.fill(0, 150, 0);
        applet.rect(applet.width - applet.textWidth(sessionName) - 30, lineHeight * 0.1f, 10, lineHeight * 0.8f);

        //tabs
        applet.textAlign(CENTER, CENTER);
        applet.fill(50);
        applet.rect(0, lineHeight, applet.width, lineHeight);
        float tabSize = applet.width / tabNames.size();
        for (int i = 0; i < tabNames.size(); i++) {
            if (i == activeTabIndex) {
                applet.fill(30);
                applet.rect(i * tabSize, lineHeight, tabSize, lineHeight);
            }
            applet.fill(255);
            applet.text(tabNames.get(i), i * tabSize + tabSize / 2f, lineHeight * 1.5f);
        }

        //draw panel
        ExtensionPanel panel = panels.get(activeTabIndex);
        applet.translate(panel.getPosX(), panel.getPosY());
        panel.drawPanel();
        applet.translate(-panel.getPosX(), -panel.getPosY());
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
    public void mousePressed(int mouseButton, int mouseX, int mouseY) {
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;
        if (mouseY > lineHeight && mouseY < lineHeight * 2) {
            float tabSize = applet.width / tabNames.size();
            int clickedIndex = (int) ((mouseX - (mouseX % tabSize)) / tabSize);
            activeTabIndex = clickedIndex;
            applet.forceRedraw();
        }
    }

    @Override
    public void mouseReleased(int mouseButton, int mouseX, int mouseY) {

    }

    @Override
    public void mouseWheel(int count) {
        ExtensionPanel panel = panels.get(activeTabIndex);
        panel.mouseWheel(count);
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);

        int ww = applet.width;
        int hh = applet.height - LookAndFeel.get().LINE_HEIGHT * 2;

        for (ExtensionPanel panel : panels) {
            panel.resize(ww, hh);
            panel.setPosition(0, LookAndFeel.get().LINE_HEIGHT * 2);
        }
    }

    @Override
    public void setPApplet(CustomPApplet applet) {
        super.setPApplet(applet);
        for (ExtensionPanel panel : panels) {
            panel.setPApplet(applet);
        }
    }

}
