/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol;

import racecontrol.hotkey.Hotkeys;
import racecontrol.client.AccBroadcastingClient;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import racecontrol.app.AppController;

/**
 * The base for the processing visualization.
 *
 * @author Leonard
 */
public class RaceControlApplet extends CustomPApplet {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(RaceControlApplet.class.getName());
    /**
     * Connection client.
     */
    private AccBroadcastingClient client;
    /**
     * The manager for hotkey actions.
     */
    private Hotkeys hotkey;
    /**
     * Controler for the base layer of the app.
     */
    private AppController appControler;

    /**
     * Creates a new instance of this object.
     */
    public RaceControlApplet() {
    }

    @Override
    public void settings() {
        size(1600, 900);
    }

    @Override
    public void setup() {
        LookAndFeel.init(this);
        surface.setResizable(true);
        surface.setTitle("ACC Race Control");
        PImage i = loadResourceAsPImage("/images/Logo.png");
        if (i != null) {
            surface.setIcon(i);
        }
        frameRate(30);

        //create the connection client.
        this.client = AccBroadcastingClient.getClient();
        
        this.hotkey = new Hotkeys();
        
        appControler = new AppController();
        setComponent(appControler.getGUIComponent());
    }

    @Override
    public void draw() {
        
        super.draw();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (key == ESC) {
            key = 0;
        }
        hotkey.handleHotkeys(event);
        super.keyPressed(event);
    }

    @Override
    public void exit() {
        LOG.info("Stopping Visualisation");
        //stop the client connection.
        if (client.isConnected()) {
            client.sendUnregisterRequest();
            client.stopAndKill();
        }
        super.exit();
    }

    private PImage loadResourceAsPImage(String resource) {
        try {
            BufferedImage bi = ImageIO.read(RaceControlApplet.class.getResourceAsStream(resource));
            PGraphics g = createGraphics(bi.getWidth(), bi.getHeight());
            g.beginDraw();
            Graphics2D g2d = (Graphics2D) g.getNative();
            g2d.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
            g.endDraw();
            PImage b = g.copy();
            return b;
        } catch (IOException ex) {
            return null;
        }
    }
}
