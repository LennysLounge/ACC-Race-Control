/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import processing.core.PApplet;

/**
 *
 * @author Leonard
 */
public class CustomPApplet extends PApplet {
    
    protected boolean forceRedraw;
    
    @Override
    public void text(String text, float x, float y) {
        float offset = LookAndFeel.TEXT_SIZE * LookAndFeel.FONT_BASELINE_OFFSET;
        super.text(text, x, y - offset);
    }
    
    public void forceRedraw(){
        forceRedraw = true;
    }
}
