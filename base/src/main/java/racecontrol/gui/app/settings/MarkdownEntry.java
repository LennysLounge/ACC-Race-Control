/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.RaceControlApplet;

/**
 *
 * @author Leonard
 */
public class MarkdownEntry {

    private static final Logger LOG = Logger.getLogger(MarkdownEntry.class.getName());

    /**
     * The text of the markdown line.
     */
    private String text;
    /**
     * The amount of indentation this line has.
     */
    private int indent;
    /**
     * The modifier for this line.
     */
    private Modifier modifier;
    /**
     * The line of the entry raw.
     */
    private String lineRaw;
    /**
     * The lines of this entry.
     */
    private List<String> lines = new ArrayList<>();
    /**
     * Height of this entry.
     */
    private float height = 0;
    /**
     * Width of this entry.
     */
    private float width = 0;
    /**
     * Height of a line.
     */
    private float lineHeight;
    /**
     * Size of the text.
     */
    private float textSize;
    /**
     * The offset between then top and where the entry starts to render.
     */
    private float yOffset;

    public MarkdownEntry(String text) {
        this.text = text;

        Pattern splitWhitespace = Pattern.compile("(\\s*)(#|\\*)?(.*)");
        Matcher m = splitWhitespace.matcher(text);
        if (m.matches()) {
            indent = m.group(1).length();
            modifier = Modifier.fromChar(m.group(2) == null ? "" : m.group(2));
            lineRaw = m.group(3);
            lines.add(m.group(3));
        } else {
            indent = 0;
            modifier = Modifier.NONE;
            lineRaw = "";
            lines.add("");
        }

        switch (modifier) {
            case HEADLINE:
                lineHeight = LINE_HEIGHT * 1.5f;
                textSize = TEXT_SIZE * 2f;
                break;
            default:
                lineHeight = LINE_HEIGHT * 0.7f;
                textSize = TEXT_SIZE;
                break;
        }
        height = lineHeight;
    }

    public void setWidth(float width) {
        if (width == this.width) {
            return;
        }
        this.width = width;
        PApplet applet = RaceControlApplet.getApplet();
        float x = 10 * indent;
        switch (modifier) {
            case HEADLINE:
                applet.textFont(LookAndFeel.fontMedium());
                applet.textSize(textSize);
                break;
            case LIST:
                x += lineHeight * 0.5f;
                applet.textFont(LookAndFeel.fontRegular());
                applet.textSize(textSize);
                yOffset = lineHeight * 0.25f;
                break;
            default:
                applet.textFont(LookAndFeel.fontRegular());
                applet.textSize(textSize);
                break;
        }
        lines.clear();
        if (x + applet.textWidth(lineRaw) < width) {
            lines.add(lineRaw);
        } else {
            String[] words = lineRaw.split(" ");
            String currentLine = words[0];
            for (int i = 1; i < words.length; i++) {
                String testLine = currentLine + " " + words[i];
                if (x + applet.textWidth(testLine) > width) {
                    lines.add(currentLine);
                    currentLine = " " + words[i];
                } else {
                    currentLine += " " + words[i];
                }
            }
            lines.add(currentLine);
        }
        height = lines.size() * lineHeight + yOffset;
    }

    public float getHeight() {
        return height;
    }

    public void render(PApplet applet, int x, int y) {
        float X = x + LINE_HEIGHT / 4f * indent;
        float Y = y + yOffset;

        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.noStroke();
        applet.textAlign(LEFT, CENTER);
        applet.textSize(textSize);
        if (modifier == Modifier.HEADLINE) {
            applet.textFont(LookAndFeel.fontMedium());
            applet.textSize(textSize);
            applet.stroke(COLOR_GRAY);
            applet.line(x, y + height, x + width, y + height);
            applet.noStroke();
        } else if (modifier == Modifier.LIST) {
            float size = textSize * 0.4f;
            float w = lineHeight * 0.5f;
            applet.ellipse(X + w / 2f, Y + lineHeight / 2f, size, size);
            X += w;
        }

        
        for (String line : lines) {
            applet.text(line, X, Y + lineHeight / 2f);
            Y += lineHeight;
        }
    }

    private enum Modifier {
        NONE,
        HEADLINE,
        LIST;

        public static Modifier fromChar(String mod) {
            switch (mod) {
                case "*":
                    return LIST;
                case "#":
                    return HEADLINE;
                default:
                    return NONE;
            }
        }
    }
}
