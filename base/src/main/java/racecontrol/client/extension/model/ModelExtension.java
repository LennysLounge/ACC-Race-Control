/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import racecontrol.client.ClientExtension;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;

/**
 * Updates the data model.
 *
 * @author Leonard
 */
public class ModelExtension extends ClientExtension
        implements EventListener {

    @Override
    public void onEvent(Event e) {
        var model = getWritableModel();

        if (e instanceof TrackInfoEvent) {
            var event = (TrackInfoEvent) e;
            model.trackInfo = event.getInfo();
        } else if (e instanceof RealtimeUpdateEvent) {
            var event = (RealtimeUpdateEvent) e;
            model.session.raw = event.getSessionInfo();
        }
    }

}
