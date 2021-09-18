/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import racecontrol.gui.CustomPApplet;
import racecontrol.gui.lpui.LPComponent;

/**
 *
 * @author Leonard
 */
public class PanelWindowApplet
        extends CustomPApplet {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(PanelWindowApplet.class.getName());
    /**
     * The panel this window is displaying.
     */
    private final LPComponent panel;
    /**
     * Action to be performed when closing this window.
     */
    private final List<Runnable> closeActions = new LinkedList<>();
    /**
     * Resizable.
     */
    private final boolean resizeable;

    public PanelWindowApplet(LPComponent panel, boolean resizeable) {
        super();
        this.panel = panel;
        this.resizeable = resizeable;
        PApplet.runSketch(new String[]{panel.getName()}, this);
    }

    @Override
    public void settings() {
        size((int) panel.getWidth(), (int) panel.getHeight());
    }

    @Override
    public void setup() {
        surface.setResizable(resizeable);
        surface.setTitle(panel.getName());
        PImage i = loadResourceAsPImage("/images/Logo.png");
        if (i != null) {
            surface.setIcon(i);
        }
        frameRate(30);

        setComponent(panel);

        //set the correct closing mode.
        if (getGraphics().isGL()) {
            final com.jogamp.newt.Window w = (com.jogamp.newt.Window) getSurface().getNative();
            w.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (key == ESC) {
            key = 0;
        }
        super.keyPressed(event);
    }

    /**
     * Prevent the applet from closing any other open applet.
     */
    @Override
    public void exitActual() {
        for (Runnable closeAction : closeActions) {
            closeAction.run();
        }
    }

    /**
     * Adds a closing action for this window.
     *
     * @param closeAction the closing action.
     */
    public void addCloseAction(Runnable closeAction) {
        this.closeActions.add(closeAction);
    }

    /**
     * Moves this window on top and grabs the focus.
     */
    public void grabFocus() {
        surface.setAlwaysOnTop(true);
        surface.setAlwaysOnTop(false);
    }
}
