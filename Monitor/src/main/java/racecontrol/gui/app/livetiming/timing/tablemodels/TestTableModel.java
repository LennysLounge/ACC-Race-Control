/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import racecontrol.gui.lpui.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class TestTableModel
        extends QualifyingTableModel {

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
    public String getName() {
        return "Test";
    }

}
