/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation;

import java.util.logging.Logger;
import processing.core.PFont;
import racecontrol.visualisation.gui.LPBase;

/**
 *
 * @author Leonard
 */
public class CustomPApplet extends LPBase {

    public static final Logger LOG = Logger.getLogger(CustomPApplet.class.getName());

    protected boolean forceRedraw;

    @Override
    public void text(String text, float x, float y) {
        float offset = LookAndFeel.TEXT_SIZE * LookAndFeel.FONT_BASELINE_OFFSET;
        super.text(text, x, y - offset);
    }

    public void forceRedraw() {
        forceRedraw = true;
    }
}
