/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import racecontrol.CustomPApplet;
import racecontrol.RaceControlApplet;
import racecontrol.lpgui.gui.LPComponent;

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

    /**
     * Moves this window on top and grabs the focus.
     */
    public void grabFocus() {
        surface.setAlwaysOnTop(true);
        surface.setAlwaysOnTop(false);
    }
}
