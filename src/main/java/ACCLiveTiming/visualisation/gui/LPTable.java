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
    /**
     * Indicates that the mouse is above the scrollbar.
     */
    private boolean mouseAboveScrollBar = false;
    /**
     * The row the mouse is over right now.
     */
    private int mouseAboveRow = -1;
    /**
     * Indicates that the scroll bar is beeing draged by the user.
     */
    private boolean dragScrollbar = false;
    /**
     * y position of the mouse when dragging the scrolbar.
     */
    private int dragScrollbarY;
    /**
     * The scroll ammount before draging the scrollbar.
     */
    private int dragScrollbarScrollAmmount;
    /**
     * Indicates that the bottom row will be drawsn beyond the boundaries of thi
     * component.
     *
     */
    private boolean drawBottomRow = false;

    private final static Renderer standardCellRenderer = new LPTable.Renderer() {
    };

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());

        applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
        applet.rect(0, 0, getWidth(), LookAndFeel.LINE_HEIGHT);
        //Draw headers
        for (Column c : columns) {
            applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
            applet.noStroke();
            applet.rect(c.xOffset, 0, c.size, LookAndFeel.LINE_HEIGHT);
            applet.fill(255);
            applet.textAlign(c.alignment, CENTER);
            int padding = (int) (LookAndFeel.LINE_HEIGHT * 0.2f);
            if (c.alignment == LEFT) {
                applet.text(c.head, c.xOffset + padding, LookAndFeel.LINE_HEIGHT / 2f);
            }
            if (c.alignment == CENTER) {
                applet.text(c.head, c.xOffset + c.size / 2f, LookAndFeel.LINE_HEIGHT / 2f);
            }
            if (c.alignment == RIGHT) {
                applet.text(c.head, c.xOffset + c.size - padding, LookAndFeel.LINE_HEIGHT / 2f);
            }
        }

        //Draw entries.
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
                int row = rowCount - scroll + 1;
                applet.translate(c.xOffset, row * LookAndFeel.LINE_HEIGHT);
                c.renderer.setWidth((int) c.size);
                c.renderer.setHeight(LookAndFeel.LINE_HEIGHT);
                c.renderer.setIsOdd(rowCount % 2 == 1);
                c.renderer.setIsMouseOverRow(false);
                c.renderer.setIsFocused(false);
                c.renderer.setIsMouseOverRow(row == mouseAboveRow);
                c.renderer.setApplet(applet);
                c.renderer.setColumn(c);
                c.renderer.draw(entry);
                applet.translate(-c.xOffset, -row * LookAndFeel.LINE_HEIGHT);
            }
        }
        //Draw scrollbar.
        if (displayScrollBar) {
            if (!drawBottomRow) {
                float lowest = getHeight() - (getHeight() % LookAndFeel.LINE_HEIGHT);
                boolean isOdd = (scroll + visibleEntries % 2) == 1;
                applet.fill(isOdd ? 40 : 50);
                applet.rect(0, lowest, getWidth(), getHeight() - lowest);
            }
            float entryHeigth = getHeight() / entries.size();
            float barHeight = entryHeigth * visibleEntries;
            float yoffset = entryHeigth * scroll;
            float padding = scrollBarWidth * 0.2f;
            applet.noStroke();
            applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
            applet.rect(0, 0, scrollBarWidth, getHeight());
            applet.stroke(70);
            applet.line(scrollBarWidth / 2, 0, scrollBarWidth / 2, getHeight());
            applet.noStroke();
            if (mouseAboveScrollBar || dragScrollbar) {
                applet.fill(LookAndFeel.COLOR_WHITE);
            } else {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            applet.rect(padding, yoffset + padding, scrollBarWidth - padding * 2, barHeight - padding * 2);
        }
    }

    public void addColumn(String head, int size, boolean dynamicSize) {
        addColumn(head, size, dynamicSize, LEFT, (e) -> "");
    }

    public void addColumn(String head,
            int size,
            boolean dynamicSize,
            int alignment,
            Function<T, String> content) {
        addColumn(head, size, dynamicSize, alignment, content, standardCellRenderer);
    }

    public void addColumn(String head,
            int size,
            boolean dynamicSize,
            Renderer renderer) {
        addColumn(head, size, dynamicSize, LEFT, (e) -> "", renderer);
    }

    private void addColumn(String head,
            int size,
            boolean dynamicSize,
            int alignment,
            Function<T, String> content,
            Renderer renderer) {
        Column column = new Column(head, size, dynamicSize, alignment, content,
                renderer);
        columns.add(column);
    }

    public void addEntry(T entry) {
        entries.add(entry);
        displayScrollBar = visibleEntries < entries.size();
        calculateColumnWidths();
    }

    public void removeEntry(T entry) {
        entries.remove(entry);
        displayScrollBar = visibleEntries < entries.size();
        calculateColumnWidths();
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
        displayScrollBar = visibleEntries < entries.size();
        calculateColumnWidths();
    }

    public void drawBottomRow(boolean state) {
        drawBottomRow = state;
    }

    @Override
    public void onResize(int w, int h) {
        visibleEntries = (int) Math.floor(getHeight() / LookAndFeel.LINE_HEIGHT) - 1;
        displayScrollBar = visibleEntries < entries.size();
        calculateColumnWidths();
        //limit scroll
        setScroll(scroll);
    }

    @Override
    public void mouseScroll(int scrollDir) {
        setScroll(scroll + scrollDir);
        invalidate();
    }

    private void setScroll(int s) {
        int maxScroll = Math.max(0, entries.size() - visibleEntries);
        scroll = Math.max(Math.min(s, maxScroll), 0);
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

    @Override
    public void onMouseMove(int x, int y) {
        if (displayScrollBar) {
            if (mouseX() < scrollBarWidth) {
                mouseAboveScrollBar = true;
            } else {
                mouseAboveScrollBar = false;
            }
        }

        int mouseRow = y / LookAndFeel.LINE_HEIGHT;
        mouseAboveRow = mouseAboveScrollBar ? -1 : mouseRow;

        if (dragScrollbar) {
            int diff = y - dragScrollbarY;
            float entrySize = (getHeight() / visibleEntries);
            int scrolldiff = (int) (diff / entrySize);
            setScroll(dragScrollbarScrollAmmount + scrolldiff);
        }
    }

    @Override
    public void onMouseLeave() {
        mouseAboveScrollBar = false;
    }

    @Override
    public void mousePressed(int x, int y, int button) {
        if (mouseAboveScrollBar) {
            dragScrollbar = true;
            dragScrollbarY = y;
            dragScrollbarScrollAmmount = scroll;
        }
    }

    @Override
    public void mouseReleased(int x, int y, int button) {
        dragScrollbar = false;
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

    /**
     * Base entry class used for the entries in this table.
     */
    public static class Entry {
    }

    /**
     * Renderer class used to render the cells.
     */
    public static abstract class Renderer {

        /**
         * The column for this render
         */
        protected LPTable.Column column;
        /**
         * Width and height of this cell.
         */
        protected int width;
        protected int height;
        /**
         * applet for this renderer.
         */
        protected PApplet applet;
        /**
         * indicates that this cell is on an odd row.
         */
        protected boolean isOdd;
        /**
         * Indicates that the mouse is currently over this row.
         */
        protected boolean isMouseOverRow;
        /**
         * Indicates that this cell is curently focused.
         */
        protected boolean isFocused;

        public void setColumn(LPTable.Column column) {
            this.column = column;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setApplet(PApplet applet) {
            this.applet = applet;
        }

        public void setIsOdd(boolean isOdd) {
            this.isOdd = isOdd;
        }

        public void setIsMouseOverRow(boolean isMouseOverRow) {
            this.isMouseOverRow = isMouseOverRow;
        }

        public void setIsFocused(boolean isFocused) {
            this.isFocused = isFocused;
        }

        public void draw(LPTable.Entry entry) {
            applet.noStroke();
            applet.fill(isOdd ? LookAndFeel.COLOR_MEDIUM_DARK_GRAY : LookAndFeel.COLOR_DARK_GRAY);
            applet.rect(0, 0, width, height);
            if (isMouseOverRow) {
                applet.fill(LookAndFeel.TRANSPARENT_RED);
                applet.rect(0, 0, width, height);
            }
            applet.fill(255);
            int padding = (int) (height * 0.2f);
            applet.textAlign(column.alignment, CENTER);
            String text = (String) column.contentFunction.apply(entry);
            if (column.alignment == LEFT) {
                applet.text(text, padding, height / 2f);
            }
            if (column.alignment == CENTER) {
                applet.text(text, width / 2f, height / 2f);
            }
            if (column.alignment == RIGHT) {
                applet.text(text, width - padding, height / 2f);
            }
        }

    }

}
