/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import static base.screen.visualisation.gui.LPComponent.applet;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.HAND;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LPLabel
        extends LPComponent {

    /**
     * The text the label is showing.
     */
    private String text;
    /**
     * Text align horizontal.
     */
    private int hAlign = LEFT;
    /**
     * Text align vertical.
     */
    private int vAlign = CENTER;

    public LPLabel(String text) {
        this.text = text;
        this.setSize(100,LINE_HEIGHT);
    }

    public void setText(String text) {
        this.text = text;
        applet.textFont(LookAndFeel.fontMedium());
        this.setSize(applet.textWidth(text), LookAndFeel.LINE_HEIGHT);
    }

    public void setHAlign(int hAlign) {
        this.hAlign = hAlign;
    }
   

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.noStroke();
        //applet.stroke(0);
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(LookAndFeel.COLOR_WHITE);
        applet.textAlign(hAlign, vAlign);
        applet.textFont(LookAndFeel.fontRegular());
        if (hAlign == LEFT) {
            applet.text(text, 0, getHeight()/2f);
        } else if (hAlign == CENTER) {
            applet.text(text, getWidth()/2f, getHeight()/2f);
        }
    }

}
