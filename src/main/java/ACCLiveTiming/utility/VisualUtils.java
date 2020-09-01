/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.utility;

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
public class VisualUtils {

    public static final int COLOR_RED = -2097152;
    public static final int COLOR_PRACTICE = -569007;
    public static final int COLOR_QUALIFYING = -10703361;
    public static final int COLOR_RACE = -8788893;
    public static final int TEXT_SIZE = 25;
    public static final int LINE_HEIGHT = 40;
    public static final float FONT_BASELINE_OFFSET = 0.15f;

    public static void text(PApplet context, String text, float x, float y) {
        float offset = TEXT_SIZE * FONT_BASELINE_OFFSET;
        context.text(text, x, y - offset);
    }

    public static void text(PGraphics context, String text, float x, float y) {
        float offset = TEXT_SIZE * FONT_BASELINE_OFFSET;
        context.text(text, x, y - offset);
    }

    public static PFont getFont(PApplet context) {
        try {
            URL res = VisualUtils.class.getResource("/fonts/Heebo/static/Heebo-Medium.ttf");
            File fontFile = Paths.get(res.toURI()).toFile();
            return context.createFont(fontFile.getAbsolutePath(), TEXT_SIZE, true);
        } catch (Exception e) {
            return context.createFont("Arial", TEXT_SIZE, true);
        }
    }

    public static void drawScrollBar(PGraphics context, int listLength, int visibleItems, int scrollAmmount) {
        int barHeight = context.height;
        int barWidth = 20;
        int padding = 3;
        
        float itemHeight = barHeight * 1f / listLength * 1f;
        context.fill(30);
        context.noStroke();
        context.rect(0, 0, barWidth, barHeight);
        context.fill(COLOR_RED);
        context.noStroke();
        context.rect(padding, itemHeight * scrollAmmount,
                barWidth - padding * 2, itemHeight * visibleItems);
    }

}
