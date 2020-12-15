/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.eventbus;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class EventBus {
    
    private static List<EventListener> listeners = new LinkedList<>();
    
    public static void register(EventListener listener){
        listeners.add(listener);
    }
    
    public static void publish(Event e){
        listeners.forEach(listener->{
            listener.onEvent(e);
        });
    }
    
}
