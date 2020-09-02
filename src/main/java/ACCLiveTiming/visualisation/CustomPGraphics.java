/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import processing.awt.PGraphicsJava2D;

/**
 *
 * @author Leonard
 */
public class CustomPGraphics extends PGraphicsJava2D {

    public CustomPGraphics() {
        super();
    }

    @Override
    public void text(String text, float x, float y) {
        float offset = LookAndFeel.get().TEXT_SIZE * LookAndFeel.get().FONT_BASELINE_OFFSET;
        super.text(text, x, y - offset);
    }
}
