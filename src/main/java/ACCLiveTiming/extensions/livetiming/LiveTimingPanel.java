/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.extensions.ExtensionPanel;
import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 */
public class LiveTimingPanel extends ExtensionPanel {

    private final LiveTimingExtension extension;

    private int padding = 0;

    private Column[] columns = {
        new Column("", 1, false, LEFT),
        new Column("P", 1, false, CENTER),
        new Column("Name", 6, false, LEFT),
        new Column("", 0.4f, false, CENTER),
        new Column("#", 1.25f, false, CENTER),
        new Column("Laps", 1.5f, true, RIGHT),
        new Column("Gap", 2, true, RIGHT),
        new Column("Leader", 2, true, RIGHT),
        new Column("Delta", 2, true, RIGHT),
        new Column("Lap", 2, true, RIGHT),
        new Column("S1", 2, true, RIGHT),
        new Column("S2", 2, true, RIGHT),
        new Column("S3", 2, true, RIGHT),
        new Column("Last", 2, true, RIGHT),
        new Column("Best", 2, true, RIGHT),
        new Column("", 0.5f, false, RIGHT)
    };

    public LiveTimingPanel(LiveTimingExtension extension) {
        this.extension = extension;
        this.displayName = "LIVE TIMING";
    }

    @Override
    public void drawPanel() {
        LookAndFeel laf = LookAndFeel.get();
        calculateColumnWidths(applet.width);

        applet.textFont(LookAndFeel.get().FONT, LookAndFeel.get().TEXT_SIZE);
        applet.textAlign(LEFT, CENTER);
        applet.noStroke();

        padding = 0;
        for (int i = 0; i < columns.length; i++) {
            drawCell(applet, columns[i].head, i, 0, applet.color(30), laf.COLOR_WHITE);
        }
        padding = 1;

        List<ListEntry> sorted = extension.getSortedEntries();

        drawCell(applet, "" + extension.getSortedEntries().size(), 3, 0, applet.color(30), laf.COLOR_WHITE);

        int n = 1;
        for (ListEntry entry : sorted) {
            if (!entry.isConnected()) {
                continue;
            }

            if (entry.isFocused()) {
                drawCellBackground(applet, n, applet.color(100));
            } else {
                if (n % 2 == 0) {
                    drawCellBackground(applet, n, applet.color(40));
                }
            }

            if (entry.isConnected()) {
                if (entry.isFocused()) {
                    drawCell(applet, entry.getPosition(), 1, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                } else {
                    drawCell(applet, entry.getPosition(), 1, n, laf.COLOR_RED, laf.COLOR_WHITE);
                }
            } else {
                drawCell(applet, entry.getPosition(), 1, n, laf.COLOR_GRAY, laf.COLOR_WHITE);
            }

            drawCell(applet, entry.getName(), 2, n, laf.COLOR_NONE, laf.COLOR_WHITE);

            if (entry.isInPits()) {
                applet.textSize(12);
                drawCell(applet, "P", 3, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                applet.textSize(laf.TEXT_SIZE);
            }

            switch (entry.getCategory()) {
                case BRONZE:
                    drawCell(applet, entry.getCarNumber(), 4, n, laf.COLOR_RED, laf.COLOR_BLACK);
                    break;
                case SILVER:
                    drawCell(applet, entry.getCarNumber(), 4, n, laf.COLOR_GRAY, laf.COLOR_WHITE);
                    break;
                case GOLD:
                case PLATINUM:
                    drawCell(applet, entry.getCarNumber(), 4, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                    break;
            }

            drawCell(applet, entry.getLapCount(), 5, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, "--.--", 6, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, "--.--", 7, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getDelta(), 8, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getCurrentLap(), 9, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getSectorOne(), 10, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getSectorTwo(), 11, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getSectorThree(), 12, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getLastLap(), 13, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(applet, entry.getBestLap(), 14, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            
            n++;
        }
    }

    @Override
    public void mouseWheel(int count) {
        //scroll += count;
    }

    private void calculateColumnWidths(float width) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;
        float w = width / lineHeight;

        float staticSize = 0;
        int dynamicCount = 0;
        List<Column> dynamicColumns = new LinkedList<>();
        for (Column c : columns) {
            if (!c.dynamicSize) {
                staticSize += c.size;
            } else {
                dynamicColumns.add(c);
                dynamicCount++;
            }
        }
        final float dynamicSize = Math.max(w - staticSize, 0);
        final int count = dynamicCount;
        dynamicColumns.forEach(column -> column.size = Math.max(dynamicSize / count, column.minSize));
    }

    private void drawCellBackground(PApplet applet, int line, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = 0;
        float width = 0;
        for (Column c : columns) {
            width += c.size;
        }
        applet.fill(color);
        applet.rect(xoffset * lineHeight, line * lineHeight,
                width * lineHeight, lineHeight);
    }

    private void drawCell(PApplet applet, String text, int x, int y, int background, int forground) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = 0;
        for (int i = 0; i < x; i++) {
            xoffset += columns[i].size;
        }

        float w = columns[x].size;

        applet.fill(background);
        applet.rect(xoffset * lineHeight + padding, y * lineHeight + padding,
                w * lineHeight - padding * 2, lineHeight - padding * 2);

        switch (columns[x].alignment) {
            case LEFT:
                xoffset += 0.2f;
                break;
            case RIGHT:
                xoffset += w - 0.2f;
                break;
            case CENTER:
                xoffset += w / 2;
                break;
        }

        applet.fill(forground);
        applet.textAlign(columns[x].alignment, CENTER);
        applet.text(text, xoffset * lineHeight, y * lineHeight + lineHeight / 2);

    }

    private class Column {

        public final String head;
        public float size;
        public float minSize;
        public final boolean dynamicSize;
        public final int alignment;

        public Column(String head, float size, boolean dynamicSize, int alignment) {
            this.head = head;
            this.size = size;
            this.minSize = size;
            this.dynamicSize = dynamicSize;
            this.alignment = alignment;
        }

    }

}
