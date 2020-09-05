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
     * Main panel.
     */
    private final MainPanel mainPanel;
    /**
     * Size of the window.
     */
    private int sizeWidth;
    private int sizeHeight;

    public Visualisation(BasicAccBroadcastingClient client) {
        mainPanel = new MainPanel(this, client);
    }

    @Override
    public void settings() {
        size(1600, 900);
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
        if(width != sizeWidth || height != sizeHeight){
            onResize(width, height);
        }
        
        
        
        
        String fr = String.valueOf(round(frameRate));
        fill(0);
        rect(0, 0, textWidth(fr), 16);
        fill(255);
        textAlign(LEFT, TOP);
        text(fr, 0, 0);
    }
    
    public void onResize(int w, int h){
        sizeWidth = w;
        sizeHeight = h;
        println(w, h);
        mainPanel.onResize(w, h);
    }

    
    @Override
    public void mousePressed() {
        mainPanel.mousePressed(mouseButton, mouseX, mouseY);
    }

    
    @Override
    public void mouseReleased() {
        mainPanel.mouseReleased(mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        mainPanel.mouseWheel(event.getCount());
    }
     
}
