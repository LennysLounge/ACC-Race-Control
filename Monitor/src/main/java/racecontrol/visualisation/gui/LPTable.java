/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation.gui;

import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.COLOR_MEDIUM_DARK_GRAY;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;

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
    private final Scrollbar scrollbar = new Scrollbar();
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
    /**
     * Action to call when a cell has been clicked. Parameters are the column
     * and the row that has been clicked.
     */
    private BiConsumer<Integer, Integer> cellClickAction = (column, row) -> {
    };

    private float mouseX;
    private float mouseY;

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.noStroke();
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
                if (!columns[i].isVisible()) {
                    continue;
                }
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
            int v = (int) Math.ceil(getHeight() / LookAndFeel.LINE_HEIGHT);
            if (drawHeader) {
                v -= 1;
            }
            //we want to draw an extra line if the height of the table is not
            //devisible by the line height. To do this we round up.
            rowLimit = Math.min(model.getRowCount() - scrollbar.scroll, v);
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
            if ((row + scrollbar.scroll) % 2 == 1) {
                applet.fill(COLOR_MEDIUM_DARK_GRAY);
                applet.rect(columnOffset, (int) rowOffset, getWidth() - columnOffset, (int) rowHeight);
            }
            if (isSelectedRow) {
                applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                applet.rect(columnOffset, (int) rowOffset, getWidth() - columnOffset, (int) rowHeight);
            }
            for (int column = 0; column < columns.length; column++) {
                if (!columns[column].isVisible()) {
                    continue;
                }
                if (row + scrollbar.scroll >= model.getRowCount()) {
                    continue;
                }

                boolean isMouseOverThisColumn = column == mouseOverColumn;
                applet.translate(columnOffset, rowOffset);
                CellRenderer renderer = columns[column].getRenderer();
                Object value = model.getValueAt(column, row + scrollbar.scroll);
                if (value == null) {
                    renderer = LPTableColumn.nullRenderer;
                }
                renderer.render(applet,
                        new RenderContext(value,
                                isSelectedRow,
                                isMouseOverThisRow,
                                isMouseOverThisColumn,
                                row % 2 == 0,
                                columnWidths[column],
                                rowHeight,
                                getWidth() - scrollbar.width,
                                getHeight(),
                                columnOffset - scrollbar.width,
                                rowOffset,
                                mouseX - columnOffset,
                                mouseY - rowOffset
                        )
                );
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
            if (scrollbar.isDragged) {
                applet.rect(padding, scrollbar.elementHeight * scrollbar.scroll + headerOffset + padding + scrollbar.smoothScrollDiff,
                        scrollbar.width - padding * 2, scrollbar.elementHeight * visibleRows - padding * 2);
            } else {
                applet.rect(padding, scrollbar.elementHeight * scrollbar.scroll + headerOffset + padding,
                        scrollbar.width - padding * 2, scrollbar.elementHeight * visibleRows - padding * 2);
            }
        }

        applet.noStroke();
    }

    @Override
    public void onMouseScroll(int scrollDir) {
        scrollbar.setScroll(scrollbar.getScroll() + scrollDir);
        invalidate();
    }

    @Override
    public void onMousePressed(int x, int y, int button) {
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
            float columnX = hPivot;
            for (int i = 0; i < columnWidths.length; i++) {
                columnX += columnWidths[i];
                if (columnX > x) {
                    column = i;
                    columnX -= columnWidths[i];
                    break;
                }
            }
            //find the row the mouse has pressed.
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
                    model.onClick(column, scrolledRow, (int) (mouseX - columnX), (int) (mouseY - scrolledRow * LINE_HEIGHT - vPivot));
                    cellClickAction.accept(column, scrolledRow);
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        scrollbar.isDragged = false;
        invalidate();
    }

    @Override
    public void onMouseMove(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        boolean mouseOverScrollbar = false;
        mouseOverColumn = -1;
        mouseOverRow = -1;

        //Dragging the scrollbar has priority over the other features.
        if (scrollbar.isDragged) {
            int diff = y - scrollbar.dragHomeY;
            int scrollDiff = (int) (diff / scrollbar.elementHeight);
            scrollbar.smoothScrollDiff = (int) (diff % scrollbar.elementHeight);
            scrollbar.setScroll(scrollbar.dragHome + scrollDiff);
            invalidate();
            return;
        }

        //The table is made of 4 areas.   (0)<Nothing> | (1)<Header
        //                              (2)<Scrollbar> | (3)<rows>
        float vPivot = drawHeader ? LookAndFeel.LINE_HEIGHT : 0;
        float hPivot = scrollbar.isVisible ? scrollbar.width : 0;
        int area = (x > hPivot ? 1 : 0) + (y > vPivot ? 2 : 0);
        if (area == 2) {
            mouseOverScrollbar = true;
        }
        if (area == 1 || area == 3) {
            float xx = x - hPivot;
            //Find the column the mouse is over.
            float accu = 0;
            for (int i = 0; i < columnWidths.length; i++) {
                accu += columnWidths[i];
                if (xx < accu) {
                    if (mouseOverColumn != i) {
                        mouseOverColumn = i;
                        invalidate();
                    }
                    break;
                }
            }
            //find the row the mouse is over.
            mouseOverRow = (y / LookAndFeel.LINE_HEIGHT);
        }

        scrollbar.setIsMouseOver(mouseOverScrollbar);
    }

    @Override
    public void onMouseLeave() {
        scrollbar.isMouseOver = false;
        mouseOverColumn = -1;
        mouseOverRow = -1;
        applet.cursor(ARROW);
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

    public void setCellClickAction(BiConsumer<Integer, Integer> action) {
        this.cellClickAction = action;
    }

    public TableModel getTableModel() {
        return model;
    }

    private float[] calculateColumnWidths(LPTableColumn[] columns, float totalWidth) {
        //final widths for the columns.
        float[] widths = new float[columns.length];
        boolean[] isCalculationForIndexDone = new boolean[columns.length];

        //First find the absolute minimum width required for all columns.
        int minWidth = 0;
        for (LPTableColumn c : columns) {
            minWidth += c.getMinWidth();
            c.setVisible(true);
        }
        //remove columns with the lowest priority until the minimum width is less
        //than the totalWidth
        while (minWidth > totalWidth) {
            int lowestPriorityIndex = 0;
            for (int i = 0; i < columns.length; i++) {
                if (isCalculationForIndexDone[i]) {
                    continue;
                }
                if (columns[i].getPriority() <= columns[lowestPriorityIndex].getPriority()) {
                    lowestPriorityIndex = i;
                }
            }
            columns[lowestPriorityIndex].setVisible(false);
            minWidth -= columns[lowestPriorityIndex].getMinWidth();
            isCalculationForIndexDone[lowestPriorityIndex] = true;
        }

        //find the sum of the growth values for the remaining columns.
        float availableWidth = totalWidth;
        float growthSum = 0;
        for (int i = 0; i < columns.length; i++) {
            if (isCalculationForIndexDone[i]) {
                continue;
            }
            growthSum += columns[i].getGrowthRate();
        }

        //calculate the width of the columns based on their growth values
        //and the available space.
        boolean recalculate = true;
        while (recalculate) {
            recalculate = false;
            float growthWidth = availableWidth / growthSum;
            for (int i = 0; i < columns.length; i++) {
                if (isCalculationForIndexDone[i]) {
                    continue;
                }

                float columnWidth = growthWidth * columns[i].getGrowthRate();

                //limit columns to their min and max sizes.
                //when a row get limited, remove mark it as done and remove
                //its size from the availbe width and recalculate all other rows.
                if (columnWidth < columns[i].getMinWidth()
                        || columnWidth > columns[i].getMaxWidth()) {
                    columnWidth = Math.min(columns[i].getMaxWidth(), Math.max(columns[i].getMinWidth(), columnWidth));
                    growthSum -= columns[i].getGrowthRate();
                    availableWidth -= columnWidth;
                    isCalculationForIndexDone[i] = true;
                    recalculate = true;
                    widths[i] = columnWidth;
                    break;
                }
                widths[i] = columnWidth;
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
        private int scroll = 0;
        /**
         * difference from actual scroll position to the currently dragged
         * position to enable smooth scrolling by dragging.
         */
        private int smoothScrollDiff = 0;
        /**
         * Indicates that the mouse is hovering over the scrollbar.
         */
        private boolean isMouseOver = false;

        public void setScroll(int scroll) {
            if (scroll != this.scroll) {
                this.scroll = scroll;
                invalidate();
            }
        }

        public int getScroll() {
            return scroll;
        }

        public boolean isIsMouseOver() {
            return isMouseOver;
        }

        public void setIsMouseOver(boolean isMouseOver) {
            if (isMouseOver != this.isMouseOver) {
                this.isMouseOver = isMouseOver;
                invalidate();
            }
        }
    }

    @FunctionalInterface
    public interface CellRenderer {

        void render(PApplet applet, RenderContext context);
    }

    public class RenderContext {

        public final Object object;
        public final boolean isSelected;
        public final boolean isMouseOverRow;
        public final boolean isMouseOverColumn;
        public final boolean isOdd;
        public final float width;
        public final float height;
        public final float tableWidth;
        public final float tableHeight;
        public final float tablePosX;
        public final float tablePosY;
        public final float mouseX;
        public final float mouseY;

        public RenderContext(Object object,
                boolean isSelected,
                boolean isMouseOverRow,
                boolean isMouseOverColumn,
                boolean isOdd,
                float width,
                float height,
                float tableWidth,
                float tableHeight,
                float tablePosX,
                float tablePosY,
                float mouseX,
                float mouseY) {
            this.object = object;
            this.isSelected = isSelected;
            this.isMouseOverRow = isMouseOverRow;
            this.isMouseOverColumn = isMouseOverColumn;
            this.isOdd = isOdd;
            this.width = width;
            this.height = height;
            this.tableWidth = tableWidth;
            this.tableHeight = tableHeight;
            this.tablePosX = tablePosX;
            this.tablePosY = tablePosY;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }
}
