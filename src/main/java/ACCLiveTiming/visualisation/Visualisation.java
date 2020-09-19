/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.event.MouseEvent;

/**
 *
 * @author Leonard
 */
public class Visualisation extends CustomPApplet {

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
    /**
     * Timer since the last draw.
     */
    private int timer = 0;
    /**
     * Connection client.
     */
    private BasicAccBroadcastingClient client;

    public Visualisation(BasicAccBroadcastingClient client, int updateInterval) {
        this.client = client;
        mainPanel = new MainPanel(this, client);
        mainPanel.setPApplet(this);

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
        frameRate(30);
    }

    @Override
    public void draw() {
        if (width != sizeWidth || height != sizeHeight) {
            onResize(width, height);
        }

        int dt = (int) (1000 / frameRate);
        timer += dt;
        if (timer > client.getUpdateInterval() || forceRedraw) {
            timer = 0;
            if(forceRedraw){
                background(50);
            }
            forceRedraw = false;
            
            translate(mainPanel.getPosX(), mainPanel.getPosY());
            mainPanel.drawPanel();
            translate(-mainPanel.getPosX(), -mainPanel.getPosY());   
        }
    }

    public void onResize(int w, int h) {
        sizeWidth = w;
        sizeHeight = h;
        mainPanel.resize(w, h);
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
