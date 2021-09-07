/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.logging;

import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class LoggingPanel
        extends LPContainer
        implements LoggerListener {

    /**
     * The table that display the messages.
     */
    private final LPTable table = new LPTable();

    /**
     * Table model for the logging extension.
     */
    private final LoggingTableModel model = new LoggingTableModel();

    public LoggingPanel() {
        setName("LOGGING");

        table.setTableModel(model);
        addComponent(table);
        UILogger.register(this);
    }

    @Override
    public void drawInternal() {
        super.drawInternal();
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

    @Override
    public void messageLogged(String message) {
        model.addMessage(new LogMessage(message));
        invalidate();
    }
}
