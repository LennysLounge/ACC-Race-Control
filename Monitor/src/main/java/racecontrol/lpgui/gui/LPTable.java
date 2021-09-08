/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.lpgui.gui;

import racecontrol.LookAndFeel;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import javax.swing.text.Utilities;
import processing.core.PApplet;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.CENTER;
import static racecontrol.LookAndFeel.COLOR_MEDIUM_DARK_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;

/**
 *
 * @author Leonard
 */
public class LPTable extends LPContainer {

    private static final Logger LOG = Logger.getLogger(LPTable.class.getName());

    /**
     * Default implementation of the table model. Does nothing.
     */
    public static final LPTableModel defaultTableModel = new LPTableModel() {
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
    private LPTableModel model = defaultTableModel;
    /**
     * height of the table model.
     */
    private float modelHeight = 0;
    /**
     * Visible area height.
     */
    private float visibleHeight = 0;
    /**
     * Lowest visible index in the table.
     */
    private int lowerVisibleIndex = 0;
    /**
     * highest visible index in the table.
     */
    private int upperVisibleIndex = 0;
    /**
     * offset for how far the lowest visible row is visible.
     */
    private float lowerVisibleRowYOffset = 0;

    /**
     * Indicates that the header should be drawn.
     */
    private boolean drawHeader = true;
    /**
     * container class for scrollbar fields.
     */
    private final Scrollbar scrollbar = new Scrollbar();
    /**
     * The column the mouse is over. -1 if the mouse is not over any column.
     */
    private int mouseOverColumn = -1;
    /**
     * The row the mouse is over. -1 if the mouse is not over any row.
     */
    private int mouseOverRow = -1;
    /**
     * The calculated widths of the columns.
     */
    private float[] columnWidths = new float[0];
    /**
     * Indicates that the column headers are clickable.
     */
    private boolean clickableColumnHeaders = false;
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

        calculateVisibleArea();

        LPTableColumn[] columns = model.getColumns();

        //Draw header
        if (drawHeader) {
            float columnOffset = 0;
            if (scrollbar.isVisible) {
                columnOffset = scrollbar.width;
            }
            applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
            applet.rect(columnOffset, 0, getWidth() - columnOffset, LINE_HEIGHT);
            applet.textAlign(CENTER, CENTER);
            applet.textFont(LookAndFeel.fontMedium());
            for (int i = 0; i < columns.length; i++) {
                if (!columns[i].isVisible()) {
                    continue;
                }
                if (clickableColumnHeaders
                        && mouseOverColumn == i
                        && mouseOverRow == 0) {
                    applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                    applet.rect(columnOffset, 0, columnWidths[i], LINE_HEIGHT);
                }
                applet.fill(LookAndFeel.COLOR_WHITE);
                applet.text(columns[i].getHeader(), columnOffset + columnWidths[i] / 2f, LINE_HEIGHT / 2f);
                columnOffset += columnWidths[i];
            }
        }
        //draw scrollbar
        if (scrollbar.isVisible) {
            float headerOffset = drawHeader ? LINE_HEIGHT : 0;

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
            float scrollbarHeight = (visibleHeight / modelHeight) * (getHeight() - headerOffset);
            float scrollbarPosition = (scrollbar.scroll / modelHeight) * (getHeight() - headerOffset) + headerOffset;
            applet.rect(padding, scrollbarPosition, scrollbar.width - padding * 2, scrollbarHeight);
        }

        //draw model
        float clipHeight = drawHeader ? LINE_HEIGHT : 0;
        float clipWidth = scrollbar.isVisible ? scrollbar.width : 0;
        applet.clip(clipWidth, clipHeight, getWidth() - clipWidth, getHeight() - clipHeight);
        float heightOffset = lowerVisibleRowYOffset + clipHeight;
        for (int row = lowerVisibleIndex; row < upperVisibleIndex; row++) {
            if(row > model.getRowCount()){
                break;
            }
            
            boolean isMouseOverThisRow = row == mouseOverRow;
            float rowHeight = model.getRowHeight(row);
            if (row % 2 == 0) {
                applet.fill(COLOR_MEDIUM_DARK_GRAY);
                applet.rect(clipWidth, heightOffset, getWidth() - clipWidth, rowHeight);
            }

            boolean isSelectedRow = model.getSelectedRow() == row;
            if (isSelectedRow) {
                applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                applet.rect(clipWidth, (int) heightOffset, getWidth() - clipWidth, (int) rowHeight);
            }
            float widthOffset = clipWidth;
            for (int column = 0; column < columns.length; column++) {
                if (!columns[column].isVisible()) {
                    continue;
                }
                Object value = model.getValueAt(column, row);
                if (value == null) {
                    continue;
                }

                boolean isMouseOverThisColumn = column == mouseOverColumn;
                applet.translate(widthOffset, heightOffset);
                columns[column].getRenderer().render(applet,
                        new RenderContext(value,
                                row,
                                column,
                                isSelectedRow,
                                isMouseOverThisRow,
                                isMouseOverThisColumn,
                                row % 2 == 0,
                                columnWidths[column],
                                rowHeight,
                                getWidth() - scrollbar.width,
                                getHeight(),
                                widthOffset - scrollbar.width,
                                heightOffset,
                                mouseX - widthOffset,
                                mouseY - heightOffset
                        )
                );
                applet.translate(-widthOffset, -heightOffset);
                widthOffset += columnWidths[column];
            }
            heightOffset += rowHeight;
        }
    }

    @Override
    public void onMouseScroll(int scrollDir) {
        scrollbar.setScroll(scrollbar.scroll + scrollDir * LINE_HEIGHT);
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
            
            if (area == 1 && clickableColumnHeaders) {
                model.onHeaderClicked(column);
            }            
            if (area == 3) {            
                int clickedIndex = -1;
                float rowY = vPivot + lowerVisibleRowYOffset;
                for(int i=lowerVisibleIndex; i<=upperVisibleIndex; i++){
                    rowY += model.getRowHeight(i);
                    if(rowY > y){
                        clickedIndex = i;
                        rowY -= model.getRowHeight(i);
                        break;
                    }
                }
                
                if (clickedIndex != -1) {
                    model.onClick(column, clickedIndex, (int) (mouseX - columnX), (int) (mouseY - rowY));
                    cellClickAction.accept(column, clickedIndex);
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
            float scrollDiff = (diff/visibleHeight) * modelHeight;
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
            //Find the column the mouse is over.
            float columnX = hPivot;
            for (int i = 0; i < columnWidths.length; i++) {
                columnX += columnWidths[i];
                if (x < columnX) {
                    if( i != mouseOverColumn){
                        mouseOverColumn = i;
                        invalidate();
                    }
                    break;
                }
            }        
            //find the row the mouse is over.
            float rowY = vPivot + lowerVisibleRowYOffset;
            for(int i=lowerVisibleIndex; i<=upperVisibleIndex; i++){
                rowY += model.getRowHeight(i);
                if(rowY > y){
                    if(i != mouseOverRow){
                        mouseOverRow = i;
                        invalidate();
                    }
                    break;
                }
            }  
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

    private void calculateVisibleArea() {

        modelHeight = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            modelHeight += model.getRowHeight(i);
        }

        visibleHeight = getHeight();
        //if a header row is drawn the visible area is smaller
        if (drawHeader) {
            visibleHeight = Math.max(0, getHeight() - LINE_HEIGHT);
        }

        //enable the scrollbar if model is taller than visible area
        scrollbar.isVisible = visibleHeight < modelHeight;

        //calculate column widths
        columnWidths = calculateColumnWidths(model.getColumns(),
                getWidth() - ((scrollbar.isVisible) ? scrollbar.width : 0));

        //limit the maximum scroll
        if (scrollbar.scroll + visibleHeight > modelHeight) {
            scrollbar.scroll = modelHeight - visibleHeight;
        }
        if (scrollbar.scroll < 0) {
            scrollbar.scroll = 0;
        }
        
        //find lower and upper limits
        lowerVisibleIndex = 0;
        lowerVisibleRowYOffset = -scrollbar.scroll;
        upperVisibleIndex = 0;
        float height = 0;
        for(int i=0; i<=model.getRowCount(); i++){
            if(height <= scrollbar.scroll){
                lowerVisibleIndex = i;
                lowerVisibleRowYOffset = -(scrollbar.scroll - height);
            }
            upperVisibleIndex = i;
            if(height > scrollbar.scroll + visibleHeight){
                break;
            }
            height += model.getRowHeight(i);  
        }
    }

    /**
     * Sets the table model for this table.
     *
     * @param model The new table model.
     */
    public void setTableModel(LPTableModel model) {
        this.model = model;
    }

    public void drawHeader(boolean state) {
        this.drawHeader = state;
    }

    public void setClickableHeader(boolean state) {
        this.clickableColumnHeaders = state;
    }

    public void setCellClickAction(BiConsumer<Integer, Integer> action) {
        this.cellClickAction = action;
    }

    public LPTableModel getTableModel() {
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
        public float dragHome = 0;
        /**
         * Amount of stroll this table currently has.
         */
        private float scroll = 0;
        /**
         * difference from actual scroll position to the currently dragged
         * position to enable smooth scrolling by dragging.
         */
        private int smoothScrollDiff = 0;
        /**
         * Indicates that the mouse is hovering over the scrollbar.
         */
        private boolean isMouseOver = false;

        public void setScroll(float scroll) {
            if (scroll != this.scroll) {
                this.scroll = scroll;
                invalidate();
            }
        }

        public float getScroll() {
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
        public final int rowIndex;
        public final int columnIndex;
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
                int rowIndex,
                int columnIndex,
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
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
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
