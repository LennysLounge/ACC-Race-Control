/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
import java.util.stream.Collectors;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;
import racecontrol.client.ClientExtension;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.extension.dangerdetection.YellowFlagEvent;

/**
 * This extension listens for contact between cars during a session and creates
 * a ContactEvent for them.
 *
 * @author Leonard
 */
public class ContactExtension
        implements EventListener, ClientExtension {

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
    /**
     * Holds car data for the past time. Maps session time to a map of carId to
     * realtimeInfo.
     */
    private final Map<Integer, Map<Integer, RealtimeInfo>> history = new HashMap<>();
    /**
     * maximum time the history is saved for.
     */
    private final int HISTORY_MAX_TIME = 10000;

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
        } else if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof SessionChangedEvent) {
            history.clear();
        }
    }

    private void onRealtimeUpdate(RealtimeInfo info) {
        // add info to history.
        int sessionTime = client.getModel().getSessionInfo().getSessionTime();
        if (!history.containsKey(sessionTime)) {
            history.put(sessionTime, new HashMap<>());
        }
        history.get(sessionTime).put(info.getCarId(), info);
    }

    public void afterPacketReceived(byte type) {
        // commit any staged incident that is older than 1 second.
        if (stagedAccident != null) {
            long now = System.currentTimeMillis();
            if (now - stagedAccident.getSystemTimestamp() > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = null;
            }
        }
    }

    public void onAccident(BroadcastingEvent event) {
        // an accident event is usually 5000 ms after the contact.
        // to get an accurate timing we subtract that offset.
        int sessionTime = client.getModel().getSessionInfo().getSessionTime() - 5000;
        int replaytime = replayOffsetExtension.getReplayTimeFromSessionTime(sessionTime);
        String logMessage = "Contact: " + client.getModel().getCar(event.getCarId()).getCarNumberString()
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

    private void onSessionUpdate(SessionInfo info) {
        // remove old history data
        var iter = history.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            int t = entry.getKey();
            if (info.getSessionTime() - t > HISTORY_MAX_TIME) {
                iter.remove();
            }
        }
    }

    private void commitAccident(ContactInfo incident) {
        if (incident.getCars().size() == 1) {
            incident = findOtherCars(incident);
        }
        EventBus.publish(new ContactEvent(incident));
    }

    private ContactInfo findOtherCars(ContactInfo incident) {
        // find history entry for the session time
        final int sessionTime = incident.getSessionEarliestTime();
        int time = history.keySet().stream()
                .min((t1, t2) -> {
                    int dt1 = Math.abs(t1 - sessionTime);
                    int dt2 = Math.abs(t2 - sessionTime);
                    return ((Integer) dt1).compareTo(dt2);
                }).get();
        Map<Integer, RealtimeInfo> h = history.get(time);

        // find other car with the smallest distance
        int subjectId = incident.getCars().get(0).getCarId();
        RealtimeInfo subject = h.get(subjectId);
        CarInfo closestCar = h.values().stream()
                .filter(r -> r.getCarId() != subject.getCarId())
                .min((r1, r2) -> {
                    float d1 = Math.abs(getDistance(r1, subject));
                    float d2 = Math.abs(getDistance(r2, subject));
                    return ((Float) d1).compareTo(d2);
                })
                .map(r -> {
                    CarInfo car = client.getModel().getCar(r.getCarId());
                    return car.withRealtime(r);
                })
                .get();

        // log 
        if (closestCar != null) {
            int trackMeters = client.getModel().getTrackInfo().getTrackMeters();
            float distance = (closestCar.getRealtime().getSplinePosition()
                    - subject.getSplinePosition()) * trackMeters;
            LOG.info(String.format("Contact: ?%s\t\t%.2fm\t%s",
                    closestCar.getCarNumberString(),
                    distance,
                    TimeUtils.asDuration(time)
            ));
        }

        CarInfo other = null;
        if (other != null) {
            incident = incident.addCar(
                    incident.getSessionEarliestTime(),
                    other,
                    incident.getSystemTimestamp());
        }
        return incident;
    }

    private float getDistance(RealtimeInfo r1, RealtimeInfo r2) {
        float distance = r1.getSplinePosition() - r2.getSplinePosition();
        if (distance > 0.5f) {
            distance -= 1f;
        }
        if (distance < -0.5f) {
            distance += 1f;
        }
        return distance;
    }
}
