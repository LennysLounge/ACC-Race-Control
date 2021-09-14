/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.entries;

import racecontrol.client.data.SessionId;
import racecontrol.lpgui.gui.LPTable;
import racecontrol.lpgui.gui.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class SimpleEventEntry
        extends RaceEventEntry {

    public SimpleEventEntry(
            SessionId sessionId,
            float sessionTime,
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
