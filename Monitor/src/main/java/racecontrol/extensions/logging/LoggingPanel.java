/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.logging;

import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class LoggingPanel extends LPContainer {

    private LoggingExtension extension;
    /**
     * The table that display the messages.
     */
    private LPTable table = new LPTable();

    public LoggingPanel(LoggingExtension extension) {
        this.extension = extension;
        setName("LOGGING");

        table.setTableModel(extension.getTableModel());
        table.setOverdrawForLastLine(true);
        addComponent(table);
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }
}
