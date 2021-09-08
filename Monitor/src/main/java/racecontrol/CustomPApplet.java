/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.lpgui.gui.LPBase;

/**
 *
 * @author Leonard
 */
public class CustomPApplet extends LPBase {

    private static final Logger LOG = Logger.getLogger(CustomPApplet.class.getName());

    @Override
    public void text(String text, float x, float y) {
        float offset = LookAndFeel.TEXT_SIZE * LookAndFeel.FONT_BASELINE_OFFSET;
        super.text(text, x, y - offset);
    }
    
    

}
