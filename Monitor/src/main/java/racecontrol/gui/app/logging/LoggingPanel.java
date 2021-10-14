/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.logging;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import racecontrol.logging.LogMessage;
import racecontrol.logging.LoggerListener;
import racecontrol.logging.UILogger;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.table.LPTable;

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
        setName("Logging");

        table.setTableModel(model);
        addComponent(table);
        UILogger.register(this);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        table.setPosition(20, 0);
        table.setSize(w - 10, h);
    }

    @Override
    public void messageLogged(String message) {
        model.addMessage(new LogMessage(message));
        invalidate();
    }
}
