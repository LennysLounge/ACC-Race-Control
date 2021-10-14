/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui.table;

import java.util.ArrayList;
import java.util.List;
import racecontrol.gui.LookAndFeel;

/**
 *
 * @author Leonard
 */
public abstract class LPTableModel {

    /**
     * List of registered table model changed listners.
     */
    private final List<TableModelChangedListener> listeners = new ArrayList<>();

    /**
     * Returns the number of rows in this table model.
     *
     * @return The number of rows.
     */
    public abstract int getRowCount();

    /**
     * Returns the columns for this model.
     *
     * @return Array of table columns.
     */
    public abstract LPTableColumn[] getColumns();

    /**
     * Returns the value at the specified cell.
     *
     * @param column The column of the cell.
     * @param row The row of the cell.
     * @return The object at that cell.
     */
    public abstract Object getValueAt(int column, int row);

    /**
     * Returns the height of the row.
     *
     * @param rowIndex the index for which to get the height.
     * @return the hight of the row.
     */
    public float getRowHeight(int rowIndex) {
        return LookAndFeel.LINE_HEIGHT;
    }

    /**
     * Returns the currently selected row.
     *
     * @return the selected row.
     */
    public int getSelectedRow() {
        return -1;
    }

    /**
     * Gets called when a cell has been clicked.
     *
     * @param column the column that has been clicekd
     * @param row the row that has been clicked.
     * @param mouseX the mouse x position inside the cell.
     * @param mouseY the mouse y position inside the cell.
     */
    public void onClick(int column, int row, int mouseX, int mouseY) {
    }

    /**
     * Gets called when a header has been clicked
     *
     * @param column the column that has been clicked.
     */
    public void onHeaderClicked(int column) {
    }

    /**
     * Adds a TableModelChangedListener to this table model.
     *
     * @param listener the listener to add.
     */
    public void registerListener(TableModelChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a TableModelChangedListener from this model.
     *
     * @param listener the listener to remove.
     */
    public void unregisterListener(TableModelChangedListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the listeners that an entry was added.
     *
     * @param index the index where the entry was added.
     */
    public void entryAdded(int index) {
        listeners.forEach(l -> l.onEntryAdded(index));
    }

}
