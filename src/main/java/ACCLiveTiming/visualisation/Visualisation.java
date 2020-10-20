/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import ACCLiveTiming.visualisation.components.BasePanel;
import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.visualisation.gui.LPComponent;
import java.util.logging.Logger;

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
    
    private BasePanel basePanel;

    public Visualisation(BasicAccBroadcastingClient client, int updateInterval) {
        this.client = client;
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

        //init components.
        LPComponent.setApplet(this);
        basePanel = new BasePanel(client);
        setComponent(basePanel);
    } 

    @Override
    public void draw() {
        int dt = (int) (1000 / frameRate);
        timer += dt;
        if (timer > client.getUpdateInterval() || forceRedraw) {
            basePanel.invalidate();
        }
        super.draw();
    }
    
    public void keyPressed(){
        if(key == ESC){
            key = 0;
        }
    }
}
