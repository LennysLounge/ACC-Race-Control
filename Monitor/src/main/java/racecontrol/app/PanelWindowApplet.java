/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import racecontrol.CustomPApplet;
import racecontrol.RaceControlApplet;
import racecontrol.lpgui.gui.LPComponent;

/**
 *
 * @author Leonard
 */
public class PanelWindowApplet
        extends CustomPApplet {
    
    private static final Logger LOG = Logger.getLogger(PanelWindowApplet.class.getName());

    /**
     * The panel this window is displaying.
     */
    private final LPComponent panel;

    public PanelWindowApplet(LPComponent panel) {
        super();
        this.panel = panel;
        PApplet.runSketch(new String[]{panel.getName()}, this);
    }

    @Override
    public void settings() {
        size((int) panel.getWidth(), (int) panel.getHeight());
    }

    @Override
    public void setup() {
        surface.setResizable(true);
        surface.setTitle(panel.getName());
        PImage i = loadResourceAsPImage("/images/Logo.png");
        if (i != null) {
            surface.setIcon(i);
        }
        frameRate(30);

        setComponent(panel);
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
