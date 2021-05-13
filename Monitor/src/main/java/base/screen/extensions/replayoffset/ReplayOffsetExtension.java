/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.replayoffset;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.networking.BroadcastingEventEvent;
import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.events.SessionChanged;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.gui.LPContainer;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class ReplayOffsetExtension
        implements AccClientExtension, EventListener {

    private static final Logger LOG = Logger.getLogger(ReplayOffsetExtension.class.getName());

    private long sessionStartTimeStamp = 0;
    
    public ReplayOffsetExtension(){
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        return null;
    }

    @Override
    public void removeExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            sessionStartTimeStamp = System.currentTimeMillis();
            LOG.info("Setting session start time to now.");
        } else if (e instanceof BroadcastingEventEvent) {
            
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            //set the session start time to the time we joined the server.
            if(sessionStartTimeStamp == 0){
                int timeConnected = event.getTimeMs();
                sessionStartTimeStamp = System.currentTimeMillis() - timeConnected;
                LOG.info("Setting session start time to " + timeConnected + " back from now");
            }
            
            LOG.info("Broadcasting event! type: " + event.getType().name());
            LOG.info("connection time: " + TimeUtils.asDuration(event.getTimeMs()));
            long replayTime = System.currentTimeMillis() - sessionStartTimeStamp;
            LOG.info("replay time: " + TimeUtils.asDuration(replayTime));
            
            
        }

    }

}
