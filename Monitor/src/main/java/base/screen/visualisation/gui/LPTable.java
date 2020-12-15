/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

import base.screen.visualisation.LookAndFeel;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LPTable extends LPContainer {

    private static final Logger LOG = Logger.getLogger(LPTable.class.getName());

    /**
     * Default implementation of the table model. Does nothing.
     */
    public static final TableModel defaultTableModel = new TableModel() {
        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public LPTableColumn[] getColumns() {
            return new LPTableColumn[0];
        }

        @Override
        public Object getValueAt(int column, int row) {
            return null;
        }
    };
    /**
     * The table model for this table.
     */
    private TableModel model = defaultTableModel;
    /**
     * Indicates that the header should be drawn.
     */
    private boolean drawHeader = true;
    /**
     * container class for scrollbar fields.
     */
    private Scrollbar scrollbar = new Scrollbar();
    /**
     * Amount of table rows visible.
     */
    private int visibleRows = 0;
    /**
     * The column the mouse is over. -1 if the mouse is not over any column.
     */
    private int mouseOverColumn = -1;
    /**
     * The row the mouse is over. -1 if the mouse is not over any row.
     */
    private int mouseOverRow = -1;
    /**
     * The columns in the model.
     */
    private LPTableColumn[] columns = new LPTableColumn[0];
    /**
     * The column widths.
     */
    private float[] columnWidths = new float[0];
    /**
     * Indicates that the column headers are clickable.
     */
    private boolean clickableColumnHeaders = false;
    /**
     * Indicates that the last line will be drawn over the edge of this
     * component.
     */
    private boolean overdrawForLastLine = false;

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());

        //Calculate how may rows are visible right now.
        visibleRows = (int) (getHeight() / LookAndFeel.LINE_HEIGHT);
        if (drawHeader) {
            visibleRows -= 1;
        }

        //Calculate if the scrollbar should be visible.
        scrollbar.isVisible = model.getRowCount() > visibleRows;

        //limit the scroll to the possible area
        int maxScroll = Math.max(0, model.getRowCount() - visibleRows);
        scrollbar.scroll = Math.min(maxScroll, Math.max(0, scrollbar.scroll));

        //set size of the scrollbar.
        scrollbar.height = getHeight();
        if (drawHeader) {
            scrollbar.height -= LookAndFeel.LINE_HEIGHT;
        }
        scrollbar.elementHeight = scrollbar.height / model.getRowCount();
        scrollbar.width = 15;
        if (!scrollbar.isVisible) {
            scrollbar.width = 0;
        }

        //Update the columns from the model
        columns = model.getColumns();
        columnWidths = calculateColumnWidths(columns, getWidth() - scrollbar.width);

        float rowHeight = LookAndFeel.LINE_HEIGHT;

        //Draw header
        if (drawHeader) {
            float columnOffset = scrollbar.width;
            applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
            applet.rect(columnOffset, 0, getWidth() - columnOffset, rowHeight);
            for (int i = 0; i < columns.length; i++) {
                if (clickableColumnHeaders
                        && mouseOverColumn == i
                        && mouseOverRow == 0) {
                    applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                    applet.rect(columnOffset, 0, columnWidths[i], rowHeight);
                }
                applet.textAlign(CENTER, CENTER);
                applet.fill(LookAndFeel.COLOR_WHITE);
                applet.text(columns[i].getHeader(), columnOffset + columnWidths[i] / 2f, rowHeight / 2f);
                columnOffset += columnWidths[i];
            }
        }

        //Draw model
        int rowLimit = Math.min(model.getRowCount(), visibleRows);
        if (overdrawForLastLine) {
            rowLimit = Math.min(model.getRowCount()-scrollbar.scroll, visibleRows + 1);
        }
        for (int row = 0; row < rowLimit; row++) {
            float columnOffset = scrollbar.width;
            float rowOffset = row * rowHeight;
            boolean isMouseOverThisRow = row == mouseOverRow;
            if (drawHeader) {
                rowOffset += rowHeight;
                isMouseOverThisRow = (row + 1) == mouseOverRow;
            }
            boolean isSelectedRow = model.getSelectedRow() == (row + scrollbar.scroll);
            if ((row + scrollbar.scroll) % 2 == 0) {
                applet.fill(0, 0, 0, 25);
                applet.rect(columnOffset, (int) rowOffset, getWidth() - columnOffset, (int) rowHeight);
            }
            if (isSelectedRow) {
                applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                applet.rect(columnOffset, (int) rowOffset, getWidth() - columnOffset, (int) rowHeight);
            }
            for (int column = 0; column < columns.length; column++) {
                boolean isMouseOverThisColumn = column == mouseOverColumn;
                applet.translate(columnOffset, rowOffset);
                columns[column].getRenderer().render(applet,
                        model.getValueAt(column, row + scrollbar.scroll),
                        isSelectedRow,
                        isMouseOverThisRow,
                        isMouseOverThisColumn,
                        columnWidths[column],
                        rowHeight);
                applet.translate(-columnOffset, -rowOffset);
                columnOffset += columnWidths[column];
            }

        }
        //Draw scrollbar
        if (scrollbar.isVisible) {
            float headerOffset = drawHeader ? rowHeight : 0;

            applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
            applet.rect(0, 0, scrollbar.width, getHeight());
            applet.stroke(LookAndFeel.COLOR_DARK_GRAY);
            applet.line(scrollbar.width / 2, headerOffset, scrollbar.width / 2, getHeight());
            applet.noStroke();

            applet.fill(LookAndFeel.COLOR_RED);
            if (scrollbar.isMouseOver || scrollbar.isDragged) {
                applet.fill(LookAndFeel.COLOR_WHITE);
            }
            float padding = scrollbar.width * 0.2f;
            applet.rect(padding, scrollbar.elementHeight * scrollbar.scroll + headerOffset + padding,
                    scrollbar.width - padding * 2, scrollbar.elementHeight * visibleRows - padding * 2);
        }

        applet.noStroke();
    }

    @Override
    public void mouseScroll(int scrollDir) {
        scrollbar.scroll += scrollDir;
        invalidate();
    }

    @Override
    public void mousePressed(int x, int y, int button) {
        //The table is made of 4 areas.   (0)<Nothing> | (1)<Header
        //                              (2)<Scrollbar> | (3)<rows>
        float vPivot = drawHeader ? LookAndFeel.LINE_HEIGHT : 0;
        float hPivot = scrollbar.isVisible ? scrollbar.width : 0;
        int area = (x > hPivot ? 1 : 0) + (y > vPivot ? 2 : 0);
        if (area == 2) {
            scrollbar.isDragged = true;
            scrollbar.dragHomeY = y;
            scrollbar.dragHome = scrollbar.scroll;
        }
        if (area == 1 || area == 3) {
            float xx = x - hPivot;
            //Find the column the mouse has pressed.
            int column = -1;
            float accu = 0;
            for (int i = 0; i < columnWidths.length; i++) {
                accu += columnWidths[i];
                if (xx < accu) {
                    column = i;
                    break;
                }
            }
            //find the row the mouse hgs pressed.
            int row = (y / LookAndFeel.LINE_HEIGHT);
            if (area == 1 && clickableColumnHeaders) {
                model.onHeaderClicked(column);
            }
            if (area == 3) {
                int scrolledRow = row + scrollbar.scroll;
                if (drawHeader) {
                    scrolledRow -= 1;
                }
                if (scrolledRow < model.getRowCount() && scrolledRow >= 0) {
                    model.onClick(column, scrolledRow);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int x, int y, int button) {
        scrollbar.isDragged = false;
    }

    @Override
    public void onMouseMove(int x, int y) {
        scrollbar.isMouseOver = false;
        mouseOverColumn = -1;
        mouseOverRow = -1;

        //Dragging the scrollbar has priority over the other features.
        if (scrollbar.isDragged) {
            int diff = y - scrollbar.dragHomeY;
            int scrollDiff = (int) (diff / scrollbar.elementHeight);
            scrollbar.scroll = scrollbar.dragHome + scrollDiff;
            return;
        }

        //The table is made of 4 areas.   (0)<Nothing> | (1)<Header
        //                              (2)<Scrollbar> | (3)<rows>
        float vPivot = drawHeader ? LookAndFeel.LINE_HEIGHT : 0;
        float hPivot = scrollbar.isVisible ? scrollbar.width : 0;
        int area = (x > hPivot ? 1 : 0) + (y > vPivot ? 2 : 0);
        if (area == 2) {
            scrollbar.isMouseOver = true;
        }
        if (area == 1 || area == 3) {
            float xx = x - hPivot;
            //Find the column the mouse is over.
            float accu = 0;
            for (int i = 0; i < columnWidths.length; i++) {
                accu += columnWidths[i];
                if (xx < accu) {
                    mouseOverColumn = i;
                    break;
                }
            }
            //find the row the mouse is over.
            mouseOverRow = (y / LookAndFeel.LINE_HEIGHT);
        }
    }

    @Override
    public void onMouseLeave() {
        scrollbar.isMouseOver = false;
        mouseOverColumn = -1;
        mouseOverRow = -1;
    }

    /**
     * Sets the table model for this table.
     *
     * @param model The new table model.
     */
    public void setTableModel(TableModel model) {
        this.model = model;
    }

    public void drawHeader(boolean state) {
        this.drawHeader = state;
    }

    public void setClickableHeader(boolean state) {
        this.clickableColumnHeaders = state;
    }

    public void setOverdrawForLastLine(boolean state) {
        this.overdrawForLastLine = state;
    }

    private float[] calculateColumnWidths(LPTableColumn[] columns, float totalWidth) {
        float[] widths = new float[columns.length];
        int[] needToCalculateWidthForIndex = new int[columns.length];
        float width = totalWidth;
        float growthSum = 0;
        for (int i = 0; i < columns.length; i++) {
            growthSum += columns[i].getGrowthRate();
            needToCalculateWidthForIndex[i] = i;
        }

        boolean recalculate = true;
        while (recalculate) {
            recalculate = false;
            float growthWidth = width / growthSum;
            for (int i = 0; i < needToCalculateWidthForIndex.length; i++) {
                int index = needToCalculateWidthForIndex[i];
                if (index == -1) {
                    continue;
                }
                float columnWidth = growthWidth * columns[index].getGrowthRate();
                //limit column to max and min values.
                boolean toRemove = false;
                if (columnWidth < columns[index].getMinWidth()) {
                    columnWidth = columns[index].getMaxWidth();
                    toRemove = true;
                }
                if (columnWidth > columns[index].getMaxWidth()) {
                    columnWidth = columns[index].getMaxWidth();
                    toRemove = true;
                }
                widths[index] = columnWidth;
                if (toRemove) {
                    growthSum -= columns[index].getGrowthRate();
                    width -= columnWidth;
                    needToCalculateWidthForIndex[i] = -1;
                    recalculate = true;
                }
            }
        }
        return widths;
    }

    private class Scrollbar {

        /**
         * Indicates that the scroll bar is beeing drawn.
         */
        public boolean isVisible = false;
        /**
         * Width of the scrollbar.
         */
        public float width = 15;
        /**
         * Height of the scrollbar.
         */
        public float height = 0;
        /**
         * Height of a single scroll element.
         */
        public float elementHeight = 0;
        /**
         * Indicates that the scroll bar is beeing dragged.
         */
        public boolean isDragged = false;
        /**
         * The Y position from where the dragging starts.
         */
        public int dragHomeY = 0;
        /**
         * The scroll position from where the dragging starts.
         */
        public int dragHome = 0;
        /**
         * Amount of stroll this table currently has.
         */
        public int scroll = 0;
        /**
         * Indicates that the mouse is hovering over the scrollbar.
         */
        public boolean isMouseOver = false;
    }

    @FunctionalInterface
    public interface CellRenderer {

        void render(PApplet applet,
                Object object,
                boolean isSelected,
                boolean isMouseOverRow,
                boolean isMouseOverColumn,
                float width,
                float height);
    }
}
