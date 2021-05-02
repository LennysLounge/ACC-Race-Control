/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.visualisation;

import base.screen.visualisation.gui.LPBase;

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
