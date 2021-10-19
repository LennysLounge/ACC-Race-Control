/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.logging;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class UILogger {
    /**
     * List of the log messages.
     */
    private static final List<String> messages = new LinkedList<>();
    /**
     * List of listeners.
     */
    private static final List<LoggerListener> listeners = new LinkedList<>();
    
    public static void register(LoggerListener listener){
        listeners.add(listener);
    }
    
    public static void log(String message) {
        messages.add(message);
        listeners.forEach((listener) -> listener.messageLogged(message));
    }
    
}
