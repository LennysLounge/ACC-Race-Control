/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.logging;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class LoggingExtension
        implements AccClientExtension {

    /**
     * Table model for the logging extension.
     */
    private static final LoggingTableModel model = new LoggingTableModel();

    /**
     * List of the log messages.
     */
    private static final List<LogMessage> messages = new LinkedList<>();

    private static LPContainer panel;

    public LoggingExtension() {
        this.panel = new LoggingPanel(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    public LoggingTableModel getTableModel() {
        return model;
    }

    public static void log(String message) {
        messages.add(new LogMessage(message));
        model.setMessages(messages);
        if (panel != null) {
            panel.invalidate();
        }
    }

    @Override
    public void removeExtension() {
    }

}
