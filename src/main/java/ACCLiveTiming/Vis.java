/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.client.ExtensionPanel;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.utility.LookAndFeel;
import ACCLiveTiming.visualisation.CustomPGraphics;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 *
 * @author Leonard
 */
public class Vis extends PApplet {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(Vis.class.getName());

    /**
     * Client for the ACC connection.
     */
    private BasicAccBroadcastingClient client;
    /**
     * List of the extension panels.
     */
    private List<ExtensionPanel> panels = new LinkedList<>();
    /**
     * List with the tab names.
     */
    private List<String> tabNames = new LinkedList<>();
    /**
     * Index of the currently active tab.
     */
    private int activeTabIndex = 0;

    public Vis(BasicAccBroadcastingClient client) {
        this.client = client;
        panels = client.getPanels();
        tabNames = panels.stream()
                .map(panel -> panel.getDisplayName())
                .collect(Collectors.toList());
    }

    @Override
    public void settings() {
        size(1280, 720);

        try {
            client.sendRegisterRequest();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while sending register request", e);
        }
    }

    @Override
    public void setup() {
        LookAndFeel.init(this);
        surface.setResizable(true);
        surface.setTitle("ACC Accident Tracker");
        textFont(LookAndFeel.get().FONT);
    }

    int counter = 0;
    int n = 0;

    @Override
    public void draw() {
        background(50);

        int headerSize = LookAndFeel.get().LINE_HEIGHT * 2;
        PGraphics base = createGraphics(width, headerSize, CustomPGraphics.class.getName());
        base.beginDraw();
        drawHeader(base);
        base.endDraw();
        image(base, 0, 0);

        try {
            if (activeTabIndex >= 0 && activeTabIndex < panels.size()) {
                PGraphics context = createGraphics(width, height - headerSize,
                        CustomPGraphics.class.getName());
                context.beginDraw();
                panels.get(activeTabIndex).drawPanel(context);
                context.endDraw();
                image(context, 0, headerSize * 2);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while drawing panel.", e);
        }
    }

    private void drawHeader(PGraphics base) {
        
        String sessionTimeLeft = TimeUtils.asDurationShort(client.getModel().getSessionInfo().getSessionEndTime());
        String sessionName = sessionIdToString(client.getSessionId());
        textAlign(LEFT, CENTER);
        fill(30);
        noStroke();

        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        //First line
        rect(0, 0, width, lineHeight);
        String conId = "CON-ID: " + client.getModel().getConnectionID();
        String packetsReceived = "Packets received: " + client.getPacketCount();

        fill(255);
        LookAndFeel.text(this, conId, 10, lineHeight / 2f);
        LookAndFeel.text(this, packetsReceived, 200, lineHeight / 2f);

        
        textAlign(RIGHT, CENTER);
        LookAndFeel.text(this, sessionName, width - 10, lineHeight / 2f);
        LookAndFeel.text(this, sessionTimeLeft, width - textWidth(sessionName) - 40, lineHeight / 2f);

        fill(0, 150, 0);
        rect(width - textWidth(sessionName) - 30, lineHeight * 0.1f, 10, lineHeight * 0.8f);

        //tabs
        textAlign(CENTER, CENTER);
        float tabSize = width / tabNames.size();
        for (int i = 0; i < tabNames.size(); i++) {
            fill((i == activeTabIndex) ? 30 : 50);
            rect(i * tabSize, lineHeight, tabSize, lineHeight);

            fill(255);
            LookAndFeel.text(this, tabNames.get(i), i * tabSize + tabSize / 2f, lineHeight * 1.5f);
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
        return result + " " + sessionId.getNumber();
    }

    @Override
    public void mousePressed() {
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;
        if (mouseY > lineHeight && mouseY < lineHeight * 2) {
            float tabSize = width / tabNames.size();
            int clickedIndex = (int) ((mouseX - (mouseX % tabSize)) / tabSize);
            activeTabIndex = clickedIndex;
        }

        panels.get(activeTabIndex).mousePressed(mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        panels.get(activeTabIndex).mouseReleased(mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        panels.get(activeTabIndex).mouseWheel(event.getCount());
    }

}
