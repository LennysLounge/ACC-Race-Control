/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import java.util.ArrayList;
import java.util.List;

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
    public abstract Column[] getColumns();

    /**
     * Returns the value at the specified cell.
     *
     * @param column The column of the cell.
     * @param row The row of the cell.
     * @return The object at that cell.
     */
    public abstract Object getValueAt(int column, int row);

}
