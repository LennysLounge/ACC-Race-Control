/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class NewLPTable extends LPContainer {

    /**
     * Default implementation of the table model. Does nothing.
     */
    public static final TableModel defaultTableModel = new TableModel() {
        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public Column[] getColumns() {
            return new Column[0];
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

    @Override
    public void draw() {
        applet.noFill();
        applet.stroke(0);
        applet.rect(0, 0, getWidth(), getHeight());

        Column[] columns = model.getColumns();

        float rowHeight = LookAndFeel.LINE_HEIGHT;
        float[] columnWidths = calculateColumnWidths(columns);

        //Draw header
        if (drawHeader) {
            float columnOffset = 0;
            for (int i = 0; i < columns.length; i++) {
                applet.noFill();
                applet.rect(columnOffset, 0, columnWidths[i], rowHeight);
                applet.textAlign(LEFT, CENTER);
                applet.fill(0);
                applet.text(columns[i].getHeader(), columnOffset, rowHeight / 2f);
                columnOffset += columnWidths[i];
            }
        }

        //Draw model
        int headerOffset = drawHeader ? 1 : 0;
        for (int row = 0; row < model.getRowCount(); row++) {
            float columnOffset = 0;
            for (int column = 0; column < columns.length; column++) {
                applet.noFill();
                applet.rect(columnOffset, (row + headerOffset) * rowHeight,
                        columnWidths[column], rowHeight);
                applet.textAlign(LEFT, CENTER);
                applet.fill(0);
                applet.text(model.getValueAt(column, row).toString(),
                        columnOffset, (row + headerOffset + 0.5f) * rowHeight);
                columnOffset += columnWidths[column];
            }

        }
        applet.noStroke();
    }

    /**
     * Sets the table model for this table.
     *
     * @param model The new table model.
     */
    public void setTableModel(TableModel model) {
        this.model = model;
    }

    private float[] calculateColumnWidths(Column[] columns) {
        float[] widths = new float[columns.length];
        int[] needToCalculateWidthForIndex = new int[columns.length];

        float width = getWidth();
        
        float growthSum = 0;
        for (int i = 0; i < columns.length; i++) {
            growthSum += columns[i].getGrowthRate();
            needToCalculateWidthForIndex[i] = i;
        }
        
        
        boolean recalculate = true;
        while(recalculate){
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

}
