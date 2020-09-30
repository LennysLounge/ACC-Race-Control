/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

/**
 *
 * @author Leonard
 * @param <T> The class this table is representing.
 */
public class LPTable<T extends LPTable.Entry> extends LPComponent {

    /**
     * List of the avaiable columns in this table.
     */
    private List<Column> columns = new LinkedList<>();
    /**
     * List of current entries in the table.
     */
    private List<T> entries = new LinkedList<>();
    /**
     * The ammount of visible entries at the moment.
     */
    private int visibleEntries = 0;
    /**
     * Ammount of scroll in the table.
     */
    private int scroll = 0;
    /**
     * Flag to show it a scroll bar is visible.
     */
    private boolean displayScrollBar = false;
    /**
     * Width of the scroll bar.
     */
    private final int scrollBarWidth = 15;

    private boolean drawBottomRow = false;

    private final static Renderer standardCellRenderer
            = (applet, column, text, width, height, isOdd) -> {
                applet.noStroke();
                applet.fill(isOdd ? 40 : 50);
                applet.rect(0, 0, width, height);
                applet.fill(255);
                int padding = (int) (height * 0.2f);
                applet.textAlign(column.alignment, CENTER);
                if (column.alignment == LEFT) {
                    applet.text(text, padding, height / 2f);
                }
                if (column.alignment == CENTER) {
                    applet.text(text, width / 2f, height / 2f);
                }
                if (column.alignment == RIGHT) {
                    applet.text(text, width - padding, height / 2f);
                }
            };

    @Override
    public void draw() {
        applet.fill(50);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(30);
        applet.rect(0, 0, getWidth(), LookAndFeel.get().LINE_HEIGHT);
        //Draw headers
        for (Column c : columns) {
            applet.fill(30);
            applet.noStroke();
            applet.rect(c.xOffset, 0, c.size, LookAndFeel.get().LINE_HEIGHT);
            applet.fill(255);
            applet.textAlign(c.alignment, CENTER);
            int padding = (int) (LookAndFeel.get().LINE_HEIGHT * 0.2f);
            if (c.alignment == LEFT) {
                applet.text(c.head, c.xOffset + padding, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
            if (c.alignment == CENTER) {
                applet.text(c.head, c.xOffset + c.size / 2f, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
            if (c.alignment == RIGHT) {
                applet.text(c.head, c.xOffset + c.size - padding, LookAndFeel.get().LINE_HEIGHT / 2f);
            }
        }

        int lowerLimit = scroll;
        int upperLimit = scroll + visibleEntries;
        if (drawBottomRow) {
            upperLimit += 1;
        }
        int rowCount = -1;
        for (T entry : entries) {
            rowCount++;
            if (rowCount < lowerLimit) {
                continue;
            }
            if (rowCount >= upperLimit) {
                break;
            }
            for (Column c : columns) {
                applet.translate(c.xOffset, (rowCount - scroll + 1) * LookAndFeel.get().LINE_HEIGHT);
                c.renderer.draw(applet,
                        c,
                        c.contentFunction.apply(entry),
                        (int) c.size,
                        LookAndFeel.get().LINE_HEIGHT,
                        rowCount % 2 == 1);
                applet.translate(-c.xOffset, -(rowCount - scroll + 1) * LookAndFeel.get().LINE_HEIGHT);
            }
        }

        if (displayScrollBar) {
            if (!drawBottomRow) {
                float lowest = getHeight() - (getHeight() % LookAndFeel.get().LINE_HEIGHT);
                boolean isOdd = (scroll + visibleEntries % 2) == 1;
                applet.fill(isOdd ? 40 : 50);
                applet.rect(0, lowest, getWidth(), getHeight() - lowest);
            }
            float entryHeigth = getHeight() / entries.size();
            float barHeight = entryHeigth * visibleEntries;
            float yoffset = entryHeigth * scroll;
            float padding = scrollBarWidth * 0.2f;
            applet.noStroke();
            applet.fill(LookAndFeel.get().COLOR_DARK_DARK_GRAY);
            applet.rect(0, 0, scrollBarWidth, getHeight());
            applet.fill(LookAndFeel.get().COLOR_RED);
            applet.rect(padding, yoffset + padding, scrollBarWidth - padding * 2, barHeight - padding * 2);
        }

    }

    public void addColumn(String head,
            int size,
            boolean dynamicSize,
            int alignment,
            Function<T, String> content) {
        columns.add(new Column(head, size, dynamicSize, alignment, content,
                standardCellRenderer));
    }

    public void addEntry(T entry) {
        entries.add(entry);
    }

    public void removeEntry(T entry) {
        entries.remove(entry);
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
    }

    public void drawBottomRow(boolean state) {
        drawBottomRow = state;
    }

    @Override
    public void onResize(int w, int h) {
        visibleEntries = (int) Math.floor(getHeight() / LookAndFeel.get().LINE_HEIGHT) - 1;
        displayScrollBar = visibleEntries < entries.size();
        calculateColumnWidths();
        //limit scroll
        int maxScroll = Math.max(0, entries.size() - visibleEntries);
        scroll = Math.max(Math.min(scroll, maxScroll), 0);
    }

    @Override
    public void mouseScroll(int scrollDir) {
        int maxScroll = Math.max(0, entries.size() - visibleEntries);
        scroll = Math.max(Math.min(scroll + scrollDir, maxScroll), 0);
        invalidate();
    }

    private void calculateColumnWidths() {
        int minSize = 0;
        for (Column c : columns) {
            minSize += c.minSize;
        }
        if (getWidth() < minSize) {
            calculateColumnsAllStatic();
        } else {
            calculateColumnsDynamic();
        }
    }

    private void calculateColumnsAllStatic() {
        int width = (int) getWidth();
        int xoffset = 0;
        if (displayScrollBar) {
            width -= scrollBarWidth;
            xoffset += scrollBarWidth;
        }

        for (Column c : columns) {
            c.size = width / columns.size();
            c.xOffset = xoffset;
            xoffset += c.size;
        }
    }

    private void calculateColumnsDynamic() {
        int width = (int) getWidth();
        int xOffset = 0;
        if (displayScrollBar) {
            width -= scrollBarWidth;
            xOffset += scrollBarWidth;
        }
        float staticSize = 0;
        int dynamicCount = 0;
        List<Column> dynamicColumns = new LinkedList<>();
        List<Column> staticColumns = new LinkedList<>();
        for (Column c : columns) {
            if (!c.dynamicSize) {
                staticSize += c.minSize;
                staticColumns.add(c);
            } else {
                dynamicColumns.add(c);
                dynamicCount++;
            }
        }
        //resize dynamic columns
        final float dynamicSize = Math.max(width - staticSize, 0);
        for (Column c : dynamicColumns) {
            c.size = Math.max(dynamicSize / dynamicCount, c.minSize);
        }
        //resize static columns in case they were shrunken.
        for (Column c : staticColumns) {
            c.size = c.minSize;
        }
        //calculate offset for each column.
        for (Column c : columns) {
            c.xOffset = xOffset;
            xOffset += c.size;
        }
    }

    public class Column {

        public final String head;
        public float size;
        public float xOffset;
        public float minSize;
        public final boolean dynamicSize;
        public final int alignment;
        public final Function<T, String> contentFunction;
        public final Renderer renderer;

        public Column(String head, float size, boolean dynamicSize, int alignment,
                Function<T, String> contentFunction, Renderer renderer) {
            this.head = head;
            this.size = size;
            this.minSize = size;
            this.dynamicSize = dynamicSize;
            this.alignment = alignment;
            this.contentFunction = contentFunction;
            this.renderer = renderer;
        }
    }

    public static class Entry {
    }

    @FunctionalInterface
    public static interface Renderer {

        void draw(PApplet applet,
                LPTable.Column column,
                String text,
                int width,
                int height,
                boolean isOdd);
    }

}
