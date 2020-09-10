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
        List<ListEntry> entries = extension.getSortedEntries();

        applet.textFont(LookAndFeel.get().FONT, LookAndFeel.get().TEXT_SIZE);
        applet.textAlign(LEFT, CENTER);
        applet.noStroke();

        int visibleLines = height / laf.LINE_HEIGHT + 1;

        //Draw Header
        applet.fill(laf.COLOR_DARK_DARK_GRAY);
        applet.rect(0, 0, width, laf.LINE_HEIGHT);
        applet.fill(laf.COLOR_WHITE);
        for (int i = 0; i < columns.length; i++) {
            drawCellText(columns[i].head, columns[i], 0, laf.COLOR_WHITE);
        }

        //Draw background
        for (int i = 1; i < visibleLines; i++) {
            applet.fill(i % 2 == 0 ? laf.COLOR_DARK_DARK_GRAY : laf.COLOR_DARK_GRAY);
            applet.rect(0, i * laf.LINE_HEIGHT, width, laf.LINE_HEIGHT);
        }

        //Draw entries
        int n = 1;
        for (ListEntry entry : extension.getSortedEntries()) {
            if (entry.isFocused()) {
                applet.fill(applet.color(255, 255, 255, 100));
                applet.rect(0, n * laf.LINE_HEIGHT, width, laf.LINE_HEIGHT);
            }

            drawCellBackground(columns[1], n, entry.isFocused() ? laf.COLOR_WHITE : laf.COLOR_RED);
            drawCellText(entry.getPosition(), columns[1], n, entry.isFocused() ? laf.COLOR_BLACK : laf.COLOR_WHITE);
            drawCellText(entry.getName(), columns[2], n, laf.COLOR_WHITE);
            if (entry.isInPits()) {
                applet.textSize(12);
                drawCellBackground(columns[3], n, laf.COLOR_WHITE);
                drawCellText("P", columns[3], n, laf.COLOR_BLACK);
                applet.textSize(laf.TEXT_SIZE);
            }

            switch (entry.getCategory()) {
                case BRONZE:
                    drawCellBackground(columns[4], n, laf.COLOR_RED);
                    drawCellText(entry.getCarNumber(), columns[4], n, laf.COLOR_BLACK);
                    break;
                case SILVER:
                    drawCellBackground(columns[4], n, laf.COLOR_GRAY);
                    drawCellText(entry.getCarNumber(), columns[4], n, laf.COLOR_WHITE);
                    break;
                case GOLD:
                case PLATINUM:
                    drawCellBackground(columns[4], n, laf.COLOR_WHITE);
                    drawCellText(entry.getCarNumber(), columns[4], n, laf.COLOR_BLACK);
                    break;
            }

            drawCellText(entry.getLapCount(), columns[5], n, laf.COLOR_WHITE);
            drawCellText("--.--", columns[6], n, laf.COLOR_WHITE);
            drawCellText("--.--", columns[7], n, laf.COLOR_WHITE);
            drawCellText(entry.getDelta(), columns[8], n, laf.COLOR_WHITE);
            drawCellText(entry.getCurrentLap(), columns[9], n, laf.COLOR_WHITE);
            drawCellText(entry.getSectorOne(), columns[10], n, laf.COLOR_WHITE);
            drawCellText(entry.getSectorTwo(), columns[11], n, laf.COLOR_WHITE);
            drawCellText(entry.getSectorThree(), columns[12], n, laf.COLOR_WHITE);
            drawCellText(entry.getLastLap(), columns[13], n, laf.COLOR_WHITE);
            drawCellText(entry.getBestLap(), columns[14], n, laf.COLOR_WHITE);

            n++;
        }

    }

    @Override
    public void mouseWheel(int count) {
        //scroll += count;
    }

    private void drawCellBackground(Column c, int y, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;
        applet.fill(color);
        applet.rect(c.xOffset * lineHeight + 1, y * lineHeight + 1,
                c.size * lineHeight - 2, lineHeight - 2);
    }

    private void drawCellText(String text, Column c, int y, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = c.xOffset;
        switch (c.alignment) {
            case LEFT:
                xoffset += 0.2f;
                break;
            case RIGHT:
                xoffset += c.size - 0.2f;
                break;
            case CENTER:
                xoffset += c.size / 2;
                break;
        }
        applet.fill(color);
        applet.textAlign(c.alignment, CENTER);
        applet.text(text, xoffset * lineHeight, y * lineHeight + lineHeight / 2);
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        calculateColumnWidths();
    }

    private void calculateColumnWidths() {
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

        //calculate offset for each column.
        float xOffset = 0;
        for (Column c : columns) {
            c.xOffset = xOffset;
            xOffset += c.size;
        }
    }

    private class Column {

        public final String head;
        public float size;
        public float xOffset;
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
