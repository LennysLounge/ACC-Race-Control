/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation;

import java.awt.Font;
import java.util.logging.Logger;
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

    @Override
    public void textSize(float size) {
        Font b = (Font)LookAndFeel.fontMedium().getNative();
        super.textSize(size);
        Font a = (Font)LookAndFeel.fontMedium().getNative();
        
        //Sometimes changing the text size will change the font
        //if this happens we reload the fonts
        if(!a.getName().equals(b.getName())){
            LookAndFeel.init(this);
            LOG.warning("Font lost while changing text size!");
            textSize(size);
        }
    }
}
