/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.entries;

import racecontrol.lpgui.gui.LPTable;
import racecontrol.lpgui.gui.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class SimpleEventEntry
        extends RaceEventEntry {
    
    public SimpleEventEntry(float sessionTime,
            String typeDescriptor,
            boolean hasReplay,
            float replayTime){
        super(sessionTime, typeDescriptor, hasReplay, replayTime); 
    }

    @Override
    public LPTable.CellRenderer getInfoRenderer() {
        return LPTableColumn.nullRenderer;
    }

}
