/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
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
public class Visualisation extends PApplet {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(Visualisation.class.getName());

    /**
     * Client for the ACC connection.
     */
    private BasicAccBroadcastingClient client;
    /**
     * Index of the currently active tab.
     */
    private int activeTabIndex = 0;

    private final MainPanel headerPanel;

    public Visualisation(BasicAccBroadcastingClient client) {
        this.client = client;
        headerPanel = new MainPanel(this, client);
    }

    @Override
    public void settings() {
        size(1600, 900);

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
        frameRate(1000);
    }

    @Override
    public void draw() {
        String fr = String.valueOf(round(frameRate));
        float width = textWidth(fr);
        fill(0);
        rect(0, 0, width, 16);
        fill(255);
        textAlign(LEFT, TOP);
        text(fr, 0, 0);
    }

    /*
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

    public void keyPressed() {
        enableDraw = !enableDraw;
        LOG.info("enableDraw: " + enableDraw);
    }
     */
}
