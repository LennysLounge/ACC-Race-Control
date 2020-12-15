/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation.gui;

/**
 *
 * @author Leonard
 */
public abstract class TableModel {

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
     */
    public void onClick(int column, int row) {
    }

    /**
     * Gets called when a header has been clicked
     *
     * @param column the column that has been clicked.
     */
    public void onHeaderClicked(int column) {
    }

}
