/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public class LookAndFeel {

    private static LookAndFeel instance = null;

    public final int COLOR_RED;
    public final int COLOR_GREEN;
    public final int COLOR_WHITE;
    public final int COLOR_BLACK;
    public final int COLOR_GRAY;
    public final int COLOR_LIGHT_GRAY;
    public final int COLOR_DARK_GRAY;
    public final int COLOR_DARK_DARK_GRAY;
    
    public final int COLOR_NONE;
    public final int COLOR_PRACTICE;
    public final int COLOR_QUALIFYING;
    public final int COLOR_RACE;
    public final int TEXT_SIZE;
    public final int LINE_HEIGHT;
    public final float FONT_BASELINE_OFFSET;
    public final PFont FONT;

    public static void init(PApplet base) {
        if (instance == null) {
            instance = new LookAndFeel(base);
        }
    }

    private LookAndFeel(PApplet base) {
        COLOR_RED = base.color(224, 0, 0);
        COLOR_GREEN = base.color(0, 170, 89);
        COLOR_WHITE = base.color(255);
        COLOR_BLACK = base.color(0);
        COLOR_GRAY = base.color(102, 102, 102);
        COLOR_LIGHT_GRAY = base.color(188);
        COLOR_DARK_GRAY = base.color(50);
        COLOR_DARK_DARK_GRAY = base.color(30);

        COLOR_NONE = base.color(255, 255, 255, 0);
        COLOR_PRACTICE = base.color(247, 81, 81);
        COLOR_QUALIFYING = base.color(92, 173, 255);
        COLOR_RACE = base.color(121, 228, 99);
        TEXT_SIZE = 20;
        LINE_HEIGHT = 40;
        FONT_BASELINE_OFFSET = 0.16f;
        PFont font;
        try {
            URL res = LookAndFeel.class
                    .getResource("/fonts/Heebo/static/Heebo-Medium.ttf");
            File fontFile = Paths.get(res.toURI()).toFile();
            font = base.createFont(fontFile.getAbsolutePath(), TEXT_SIZE, true);
        } catch (Exception e) {
            font = base.createFont("Arial", TEXT_SIZE, true);
        }
        FONT = font;
    }

    public static LookAndFeel get() {
        return instance;
    }

    public static void drawScrollBar(PGraphics context, int listLength, int visibleItems, int scrollAmmount) {
        int barHeight = context.height;
        int barWidth = 20;
        int padding = 3;

        float itemHeight = barHeight * 1f / listLength * 1f;
        context.fill(30);
        context.noStroke();
        context.rect(0, 0, barWidth, barHeight);
        context.fill(instance.COLOR_RED);
        context.noStroke();
        context.rect(padding, itemHeight * scrollAmmount,
                barWidth - padding * 2, itemHeight * visibleItems);
    }

}
