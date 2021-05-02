/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
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
