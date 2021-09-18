/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui;

import racecontrol.gui.hotkey.Hotkeys;
import racecontrol.client.AccBroadcastingClient;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import processing.core.PImage;
import processing.event.KeyEvent;
import racecontrol.gui.app.AppController;
import racecontrol.gui.lpui.LPComponent;

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
     * Tasks that are to be executed on the animation thread later.
     */
    private static final List<Runnable> runLater = new LinkedList<>();
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

        LPComponent.setStaticApplet(this);

        //create the connection client.
        client = AccBroadcastingClient.getClient();
        client.initialise();

        hotkey = new Hotkeys();

        appControler = AppController.getInstance();
        appControler.initialise();
        setComponent(appControler.getGUIComponent());
    }

    @Override
    public void draw() {

        super.draw();

        synchronized (runLater) {
            for (Runnable task : runLater) {
                task.run();
            }
            runLater.clear();
        }
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
            //client.stopAndKill();
        }
        super.exit();
    }

    public static void runLater(Runnable task) {
        synchronized (runLater) {
            runLater.add(task);
        }
    }
}
