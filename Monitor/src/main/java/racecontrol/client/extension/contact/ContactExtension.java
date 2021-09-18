/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.client.data.SessionId;
import racecontrol.client.events.AfterPacketReceivedEvent;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.enums.BroadcastingEventType;
import racecontrol.utility.TimeUtils;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.AccBroadcastingClient;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;

/**
 * This extension listens for contact between cars during a session and creates
 * a ContactEvent for them.
 *
 * @author Leonard
 */
public class ContactExtension
        implements EventListener, AccBroadcastingExtension {

    /**
     * Singelton instance.
     */
    private static ContactExtension instance = null;
    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(ContactExtension.class.getName());
    /**
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;
    /**
     * Last accident that is waiting to be commited.
     */
    private ContactInfo stagedAccident = null;
    /**
     * Reference to the replay offset extension
     */
    private final ReplayOffsetExtension replayOffsetExtension;

    public static ContactExtension getInstance() {
        if (instance == null) {
            instance = new ContactExtension();
        }
        return instance;
    }

    private ContactExtension() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        replayOffsetExtension = ReplayOffsetExtension.getInstance();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof AfterPacketReceivedEvent) {
            afterPacketReceived(((AfterPacketReceivedEvent) e).getType());
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            if (event.getType() == BroadcastingEventType.ACCIDENT) {
                onAccident(event);
            }
        }
    }

    public void afterPacketReceived(byte type) {
        if (stagedAccident != null) {
            long now = System.currentTimeMillis();
            if (now - stagedAccident.getSystemTimestamp() > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = null;
            }
        }
    }

    public void onAccident(BroadcastingEvent event) {
        float sessionTime = client.getModel().getSessionInfo().getSessionTime();
        int replaytime = replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime);
        String logMessage = "Contact: #" + client.getModel().getCar(event.getCarId()).getCarNumber()
                + "\t\t" + TimeUtils.asDuration(sessionTime)
                + "\t" + TimeUtils.asDuration(replaytime);
        UILogger.log(logMessage);
        LOG.info(logMessage);

        SessionId sessionId = client.getSessionId();
        if (stagedAccident == null) {
            stagedAccident = new ContactInfo(sessionTime,
                    replaytime,
                    client.getModel().getCar(event.getCarId()),
                    sessionId);
        } else {
            float timeDif = stagedAccident.getSessionLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new ContactInfo(sessionTime,
                        replaytime,
                        client.getModel().getCar(event.getCarId()),
                        sessionId);
            } else {
                stagedAccident = stagedAccident.addCar(sessionTime,
                        client.getModel().getCar(event.getCarId()),
                        System.currentTimeMillis());
            }
        }
    }

    private void commitAccident(ContactInfo a) {
        EventBus.publish(new ContactEvent(a));
    }

}
