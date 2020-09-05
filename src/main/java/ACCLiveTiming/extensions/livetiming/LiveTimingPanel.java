/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.client.ExtensionPanel;
import ACCLiveTiming.networking.enums.DriverCategory;
import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import processing.core.PGraphics;

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
    public void drawPanel(PGraphics base) {
        LookAndFeel laf = LookAndFeel.get();
        calculateColumnWidths(base.width);

        base.textFont(LookAndFeel.get().FONT, LookAndFeel.get().TEXT_SIZE);
        base.textAlign(LEFT, CENTER);
        base.noStroke();

        padding = 0;
        for (int i = 0; i < columns.length; i++) {
            drawCell(base, columns[i].head, i, 0, base.color(30), laf.COLOR_WHITE);
        }

        padding = 1;

        base.strokeWeight(10);

        int n = 1;
        for (ListEntry entry : extension.getEntries()) {
            if (!entry.isConnected()) {
                continue;
            }

            if(entry.isFocused()){
                drawCellBackground(base, n, base.color(100));
            }
            else{
                if (n % 2 == 0) {
                    drawCellBackground(base, n, base.color(40));
                }
            }
            

            if (entry.isConnected()) {
                if (entry.isFocused()) {
                    drawCell(base, entry.getPosition(), 1, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                } else {
                    drawCell(base, entry.getPosition(), 1, n, laf.COLOR_RED, laf.COLOR_WHITE);
                }
            } else {
                drawCell(base, entry.getPosition(), 1, n, laf.COLOR_GRAY, laf.COLOR_WHITE);
            }

            drawCell(base, entry.getName(), 2, n, laf.COLOR_NONE, laf.COLOR_WHITE);

            if (entry.isInPits()) {
                base.textSize(12);
                drawCell(base, "P", 3, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                base.textSize(laf.TEXT_SIZE);
            }

            switch (entry.getCategory()) {
                case BRONZE:
                    drawCell(base, entry.getCarNumber(), 4, n, laf.COLOR_RED, laf.COLOR_BLACK);
                    break;
                case SILVER:
                    drawCell(base, entry.getCarNumber(), 4, n, laf.COLOR_GRAY, laf.COLOR_WHITE);
                    break;
                case GOLD:
                case PLATINUM:
                    drawCell(base, entry.getCarNumber(), 4, n, laf.COLOR_WHITE, laf.COLOR_BLACK);
                    break;
            }

            drawCell(base, entry.getLapCount(), 5, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getGap(), 6, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getToLeader(), 7, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getDelta(), 8, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getCurrentLap(), 9, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getSectorOne(), 10, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getSectorTwo(), 11, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getSectorThree(), 12, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getLastLap(), 13, n, laf.COLOR_NONE, laf.COLOR_WHITE);
            drawCell(base, entry.getBestLap(), 14, n, laf.COLOR_NONE, laf.COLOR_WHITE);

            n++;
        }

        /*
        int n = 1;
        float y = 0;
        float x = 0;
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;
        for (ListEntry entry : extension.getEntries()) {
            y = lineHeight * n++;
            x = 10;

            base.noStroke();
            base.fill(LookAndFeel.get().COLOR_RED);
            base.rect(x + 1, y + 1, lineHeight - 2, lineHeight - 2);

            //Position
            base.textAlign(CENTER, CENTER);
            base.fill(255);
            base.text(entry.getPosition(), x + lineHeight / 2, y + lineHeight / 2);

            //Name
            x = 10 + lineHeight + 5;
            base.textAlign(LEFT, CENTER);
            base.text(entry.getName(), x, y + lineHeight / 2);

            //Car number
            x = 300;
            float width = lineHeight * 1.25f;
            switch (entry.getCategory()) {
                case BRONZE:
                    base.fill(LookAndFeel.get().COLOR_RED);
                    break;
                case SILVER:
                    base.fill(LookAndFeel.get().COLOR_GRAY);
                    break;
                case GOLD:
                case PLATINUM:
                    base.fill(LookAndFeel.get().COLOR_WHITE);
                    break;
                default:
                    base.fill(LookAndFeel.get().COLOR_RED);
            }
            base.rect(x + 1, y + 1, width - 1, lineHeight - 2);
            if (entry.getCategory() == DriverCategory.SILVER) {
                base.fill(255);
            } else {
                base.fill(0);
            }
            base.textAlign(CENTER, CENTER);
            base.text(entry.getCarNumber(), x + width / 2, y + lineHeight / 2);

            //pit sign
            x += width;
            if (entry.isInPits()) {
                width = 12;
                base.fill(255);
                base.rect(x, y + 1, width, lineHeight - 2);
                base.fill(0);
                
                base.text("P", x + width / 2, y + lineHeight / 2);
                base.textSize(LookAndFeel.get().TEXT_SIZE);

            }

            x += 50;
            base.fill(255);
            base.textAlign(LEFT, CENTER);
            base.text(entry.getLapCount(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getGap(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getToLeader(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getDelta(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getCurrentLap(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getSectorOne(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getSectorTwo(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getSectorThree(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getBestLap(), x, y + lineHeight / 2);

            x += 100;
            base.text(entry.getLastLap(), x, y + lineHeight / 2);
            
        }

         */
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

    private void drawCellBackground(PGraphics base, int line, int color) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = 0;
        float width = 0;
        for (Column c : columns) {
            width += c.size;
        }
        base.fill(color);
        base.rect(xoffset * lineHeight, line * lineHeight,
                width * lineHeight, lineHeight);
    }

    private void drawCell(PGraphics base, String text, int x, int y, int background, int forground) {
        float lineHeight = LookAndFeel.get().LINE_HEIGHT;

        float xoffset = 0;
        for (int i = 0; i < x; i++) {
            xoffset += columns[i].size;
        }

        float w = columns[x].size;

        base.fill(background);
        base.rect(xoffset * lineHeight + padding, y * lineHeight + padding,
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

        base.fill(forground);
        base.textAlign(columns[x].alignment, CENTER);
        base.text(text, xoffset * lineHeight, y * lineHeight + lineHeight / 2);

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
