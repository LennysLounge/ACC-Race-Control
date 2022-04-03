/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.results;

import racecontrol.client.extension.contact.ContactInfo;
import racecontrol.client.protocol.BroadcastingEvent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class IncidentReport {

    public List<BroadcastingEvent> broadcastEvents = new LinkedList<>();

    public List<ContactInfo> incidents = new LinkedList<>();

    public long greenFlagOffset = 0;

}
