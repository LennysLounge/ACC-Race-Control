/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.results;

import base.screen.extensions.incidents.IncidentInfo;
import base.screen.networking.data.BroadcastingEvent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class IncidentReport {
    
    public List<BroadcastingEvent> broadcastEvents = new LinkedList<>();
    
    public List<IncidentInfo> incidents = new LinkedList<>();
    
    public long greenFlagOffset = 0;
    
}
