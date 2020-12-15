/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.visualisation;

import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author Leonard
 */
public class LookAndFeel {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(LookAndFeel.class.getName());

    private static LookAndFeel instance = null;

    public static final int COLOR_RED = 0xffe00000;
    public static final int COLOR_DARK_RED = 0xffaa0000;
    public static final int COLOR_GREEN = 0xff00aa59;
    public static final int COLOR_WHITE = 0xffffffff;
    public static final int COLOR_BLACK = 0xff000000;
    public static final int COLOR_GRAY = 0xff666666;
    public static final int COLOR_LIGHT_GRAY = 0xffbcbcbc;
    public static final int COLOR_DARK_GRAY = 0xff323232;
    public static final int COLOR_MEDIUM_DARK_GRAY = 0xff1e1e1e;
    public static final int COLOR_DARK_DARK_GRAY = 0xff0e0e0e;
    public static final int COLOR_PRACTICE = 0xfff75151;
    public static final int COLOR_QUALIFYING = 0xff5cadff;
    public static final int COLOR_RACE = 0xff79e463;
    public static final int COLOR_YELLOW = 0xffaaaa00;
    public static final int TRANSPARENT_WHITE = 0x14ffffff;
    public static final int TRANSPARENT_RED = 0x96ff0000;
    public static final int TEXT_SIZE = 20;
    public static final int LINE_HEIGHT = 40;
    public static final float FONT_BASELINE_OFFSET = 0.16f;
    
    private static PFont FONT;

    public static void init(PApplet base) {
        PFont font;
        try {
            font = base.createFont("font/Heebo-Medium.ttf", TEXT_SIZE, true);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Font not found.", e);
            font = base.createFont("Arial", TEXT_SIZE, true);
        }
        FONT = font;
    }

    public static PFont font(){
        return FONT;
    }
}
