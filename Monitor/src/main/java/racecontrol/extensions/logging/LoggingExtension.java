/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.logging;

import racecontrol.client.extension.AccClientExtension;
import racecontrol.visualisation.gui.LPContainer;
import java.util.LinkedList;
import java.util.List;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class LoggingExtension
        extends AccClientExtension {

    /**
     * Table model for the logging extension.
     */
    private final LoggingTableModel model = new LoggingTableModel();

    /**
     * List of the log messages.
     */
    private final List<LogMessage> messages = new LinkedList<>();

    private final LPContainer panel;

    public LoggingExtension(AccBroadcastingClient client) {
        super(client);
        this.panel = new LoggingPanel(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    public LoggingTableModel getTableModel() {
        return model;
    }

    public void log(String message) {
        messages.add(new LogMessage(message));
        model.setMessages(messages);
        if (panel != null) {
            panel.invalidate();
        }
    }

    @Override
    public void onEvent(Event e) {
    }
    
}
