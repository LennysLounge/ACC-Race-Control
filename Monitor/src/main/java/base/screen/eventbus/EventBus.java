/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.eventbus;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class EventBus {

    private static final List<EventListener> listeners = new LinkedList<>();

    private final static Object syncObject = new Object();

    public static void register(EventListener listener) {
        synchronized (syncObject) {
            listeners.add(listener);
        }
    }

    public static void unregister(EventListener listener) {
        synchronized (syncObject) {
            listeners.remove(listener);
        }
    }

    public static void publish(Event e) {
        synchronized (syncObject) {
            listeners.forEach(listener -> {
                listener.onEvent(e);
            });
        }
    }
}
