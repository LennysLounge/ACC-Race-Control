/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui;

import java.util.HashMap;
import racecontrol.gui.hotkey.Hotkeys;
import racecontrol.client.AccBroadcastingClient;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import processing.core.PImage;
import processing.event.KeyEvent;
import racecontrol.Main;
import racecontrol.gui.app.AppController;
import racecontrol.gui.app.PanelWindowApplet;
import racecontrol.gui.lpui.LPComponent;

/**
 * The base for the processing visualization.
 *
 * @author Leonard
 */
public class RaceControlApplet extends CustomPApplet {

    /**
     * Singelton instance.
     */
    private static RaceControlApplet instance;
    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(RaceControlApplet.class.getName());
    /**
     * Tasks that are to be executed on the animation thread later.
     */
    private static final List<Runnable> runLater = new LinkedList<>();
    /**
     * Map of components to their window applet.
     */
    private static final Map<LPComponent, PanelWindowApplet> windowPanels = new HashMap<>();
    /**
     * Connection client.
     */
    private AccBroadcastingClient client;
    /**
     * The manager for hotkey actions.
     */
    private Hotkeys hotkey;

    /**
     * Gets an instance of this object.
     *
     * @return The instance of this object.
     */
    public static RaceControlApplet getApplet() {
        if (instance == null) {
            instance = new RaceControlApplet();
        }
        return instance;
    }

    private RaceControlApplet() {
    }

    @Override
    public void settings() {
        size(1600, 900);
    }

    @Override
    public void setup() {
        Thread.setDefaultUncaughtExceptionHandler(new Main.UncoughtExceptionHandler());

        LookAndFeel.init(this);
        surface.setResizable(true);
        surface.setTitle("ACC Race Control");
        PImage i = loadResourceAsPImage("/images/Logo.png");
        if (i != null) {
            surface.setIcon(i);
        }
        frameRate(60);

        LPComponent.setStaticApplet(this);

        //create the connection client.
        client = AccBroadcastingClient.getClient();
        client.initialise();

        hotkey = new Hotkeys();

        setComponent(new AppController().getGUIComponent());
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
        super.keyPressed(event);
    }

    @Override
    public void keyPressedFallthrough(KeyEvent event) {
        hotkey.handleHotkeys(event);
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

    /**
     * Adds a task to a queue to run after the current UI event loop has been
     * processed.
     *
     * @param task The task to run.
     */
    public static void runLater(Runnable task) {
        synchronized (runLater) {
            runLater.add(task);
        }
    }

    /**
     * Creates a new window for a panel. If a window for the given panel already
     * exists it grabs focus for that window.
     *
     * @param panel The panel to create a window for.
     * @param resizeable Is that panel resizable.
     * @return The applet for that panel.
     */
    public static PanelWindowApplet launchNewWindow(LPComponent panel,
            boolean resizeable) {
        // only create a window if that panel doesnt already have one.
        if (!windowPanels.containsKey(panel)) {
            panel.setPosition(0, 0);
            panel.setVisible(true);
            panel.setEnabled(true);
            PanelWindowApplet applet = new PanelWindowApplet(panel, resizeable);
            applet.addCloseAction(() -> {
                windowPanels.remove(panel);
            });
            windowPanels.put(panel, applet);
        } else {
            windowPanels.get(panel).grabFocus();
        }
        return windowPanels.get(panel);
    }

    /**
     * Finds a window for a given component. If no window for the component
     * exists, returns null.
     *
     * @param panel The component to find the matching window for.
     * @return The panel window for the component.
     */
    public static PanelWindowApplet getPanelWindow(LPComponent panel) {
        return windowPanels.get(panel);
    }
}
