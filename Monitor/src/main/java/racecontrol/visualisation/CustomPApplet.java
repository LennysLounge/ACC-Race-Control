/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation;

import racecontrol.visualisation.gui.LPBase;

/**
 *
 * @author Leonard
 */
public class CustomPApplet extends LPBase {

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
