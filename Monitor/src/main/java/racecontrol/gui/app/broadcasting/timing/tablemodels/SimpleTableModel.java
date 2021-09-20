/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.broadcasting.timing.tablemodels;

import racecontrol.gui.lpui.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class SimpleTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
    }

}
