/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.extensions.logging;

import acclivetiming.Monitor.extensions.AccClientExtension;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class LoggingExtension extends AccClientExtension {
    
    /**
     * Table model for the logging extension.
     */
    private static final LoggingTableModel model = new LoggingTableModel();
    
    /**
     * List of the log messages.
     */
    private static final List<LogMessage> messages = new LinkedList<>();

    public LoggingExtension() {
        this.panel = new LoggingPanel(this);
    }
    
    public LoggingTableModel getTableModel(){
        return model;
    }
    
    public static void log(String message){
        messages.add(new LogMessage(message));
        model.setMessages(messages);
    }
}
