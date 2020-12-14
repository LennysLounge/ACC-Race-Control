/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.visualisation;

import ACCLiveTiming.monitor.visualisation.components.BasePanel;
import ACCLiveTiming.monitor.networking.PrimitivAccBroadcastingClient;
import java.util.logging.Logger;

/**
 * The base for the processing visualization.
 *
 * @author Leonard
 */
public class Visualisation extends CustomPApplet {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(Visualisation.class.getName());
    /**
     * Timer since the last draw.
     */
    private int timer = 0;
    /**
     * Connection client.
     */
    private PrimitivAccBroadcastingClient client;
    /**
     * The base panel to use.
     */
    private BasePanel basePanel;

    /**
     * Creates a new instance of this object.
     * @param client The ACC client connection to use.
     */
    public Visualisation(PrimitivAccBroadcastingClient client) {
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

    @Override
    public void keyPressed() {
        if (key == ESC) {
            key = 0;
        }
    }
    
    @Override
    public void exit(){
        println("Stopping");
        client.sendUnregisterRequest();
        super.exit();
    }
}
