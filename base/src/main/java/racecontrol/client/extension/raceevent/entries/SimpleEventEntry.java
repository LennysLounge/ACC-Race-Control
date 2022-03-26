/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.raceevent.entries;

import racecontrol.client.data.SessionId;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class SimpleEventEntry
        extends RaceEventEntry {

    public SimpleEventEntry(
            SessionId sessionId,
            int sessionTime,
            String typeDescriptor,
            boolean hasReplay) {
        super(sessionId, sessionTime, typeDescriptor, hasReplay);
    }

    @Override
    public LPTable.CellRenderer getInfoRenderer() {
        return LPTableColumn.nullRenderer;
    }

    @Override
    public String getInfo() {
        return "";
    }
    

}
