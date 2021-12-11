/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import processing.core.PGraphics;
import processing.core.PImage;
import racecontrol.gui.lpui.LPBase;

/**
 *
 * @author Leonard
 */
public class CustomPApplet extends LPBase {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(CustomPApplet.class.getName());
    /**
     * Stack holds the current clip and transform.
     */
    private final Stack<ClipTranslate> clips = new Stack<>();

    @Override
    public void text(String text, float x, float y) {
        float offset = LookAndFeel.TEXT_SIZE * LookAndFeel.FONT_BASELINE_OFFSET;
        super.text(text, x, y - offset);
    }

    /**
     * Clips and translate the drawing to the specified area.
     *
     * @param x X position
     * @param y Y position
     * @param w Width of area
     * @param h Height of area
     * @return A lock object used to reverse this clip.
     */
    public Object pushClip(float x, float y, float w, float h) {
        ClipTranslate newClip = new ClipTranslate(x, y, 0, 0, w, h);
        if (!clips.isEmpty()) {
            // translate new clip to old reference frame
            newClip.x += newClip.tx;
            newClip.y += newClip.ty;
            
            // turn width and height into end points
            newClip.w = newClip.x + newClip.w;
            newClip.h = newClip.y + newClip.h;

            // constrain new clip to old clip area
            ClipTranslate prevClip = clips.peek();
            newClip.x = constrain(newClip.x, prevClip.x, prevClip.x + prevClip.w);
            newClip.y = constrain(newClip.y, prevClip.y, prevClip.y + prevClip.h);
            newClip.w = constrain(newClip.w, prevClip.x, prevClip.x + prevClip.w);
            newClip.h = constrain(newClip.h, prevClip.y, prevClip.y + prevClip.h);
            
            // turn width and height back to width and height
            newClip.w = newClip.w - newClip.x;
            newClip.h = newClip.h - newClip.y;

            // translate new clip to new reference frame
            newClip.x -= newClip.tx;
            newClip.y -= newClip.ty;
        }

        translate(newClip.tx, newClip.ty);
        clip(newClip.x, newClip.y, newClip.w, newClip.h);
        clips.push(newClip);
        return newClip.lock;
    }

    /**
     * removes a clip from the stack.
     *
     * @param lock Lock object to authorize this pop.
     */
    public void popClip(Object lock) {
        if (clips.isEmpty()) {
            return;
        }
        ClipTranslate clipTranslate = clips.peek();
        if (clipTranslate.lock != lock) {
            return;
        }
        noClip();
        translate(-clipTranslate.tx, -clipTranslate.ty);
        clips.pop();
    }

    /**
     * Loads a resource as a PImage
     *
     * @param resource path to the resource.
     * @return PImage
     */
    public PImage loadResourceAsPImage(String resource) {
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

    private class ClipTranslate {

        float tx;
        float ty;
        float x;
        float y;
        float w;
        float h;
        Object lock;

        public ClipTranslate(float tx, float ty, float x, float y, float w, float h) {
            this.tx = tx;
            this.ty = ty;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            lock = new Object();
        }

    }

}
