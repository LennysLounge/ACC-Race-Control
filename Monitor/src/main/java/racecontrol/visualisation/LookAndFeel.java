/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation;

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
    private static final Logger LOG = Logger.getLogger(LookAndFeel.class.getName());

    public static final int COLOR_RED = 0xffe00000;
    public static final int COLOR_DARK_RED = 0xffb10808;
    public static final int COLOR_GREEN = 0xff00aa59;
    public static final int COLOR_YELLOW = 0xffaaaa00;
    public static final int COLOR_PURPLE = 0xffA256ff;
    public static final int COLOR_ORANGE = 0xfffc8800;
    public static final int COLOR_BLUE = 0xff558ccc;
    public static final int COLOR_GT4 = 0xff2626cc;
    public static final int COLOR_PORSCHE_CUP = 0xff457c45;
    public static final int COLOR_SUPER_TROFEO = 0xffccba00;
    public static final int COLOR_WHITE = 0xffffffff;
    public static final int COLOR_BLACK = 0xff000000;
    public static final int COLOR_LIGHT_GRAY = 0xffbcbcbc;
    public static final int COLOR_GRAY = 0xff3c3c3c;
    public static final int COLOR_DARK_GRAY = 0xff202020;
    public static final int COLOR_MEDIUM_DARK_GRAY = 0xff1d1d1d;
    public static final int COLOR_DARK_DARK_GRAY = 0xff0e0e0e;
    public static final int COLOR_PRACTICE = 0xfff75151;
    public static final int COLOR_QUALIFYING = 0xff5cadff;
    public static final int COLOR_RACE = 0xff79e463;
    public static final int TRANSPARENT_WHITE = 0x14ffffff;
    public static final int TRANSPARENT_RED = 0x96ff0000;
    public static final int TRANSPARENT_BLACK = 0x96000000;
    public static final int TEXT_SIZE = 20;
    public static final int LINE_HEIGHT = 40;
    public static final float FONT_BASELINE_OFFSET = 0.16f;

    private static PFont FONT_MEDIUM;
    private static PFont FONT_REGULAR;

    public static void init(PApplet base) {
        PFont fontMedium;
        PFont fontRegular;
        try {
            fontMedium = base.createFont("data/font/Heebo-SemiBold.ttf", TEXT_SIZE, true);
            fontRegular = base.createFont("data/font/Heebo-Regular.ttf", TEXT_SIZE, true);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Font not found.", e);
            fontMedium = base.createFont("Arial", TEXT_SIZE, true);
            fontRegular = base.createFont("Arial", TEXT_SIZE, true);
        }
        FONT_MEDIUM = fontMedium;
        FONT_REGULAR = fontRegular;

    }

    public static PFont fontMedium() {
        return FONT_MEDIUM;
    }

    public static PFont fontRegular() {
        return FONT_REGULAR;
    }
}
