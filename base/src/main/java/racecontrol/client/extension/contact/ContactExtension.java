/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;

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
    private final AccBroadcastingClient CLIENT;
    /**
     * Reference to the replay offset extension
     */
    private final ReplayOffsetExtension REPLAY_EXTENSION;
    /**
     * Last contact that is waiting to be commited.
     */
    private ContactInfo stagedContact = null;
    /**
     * Timestamp of when the contact was staged.
     */
    private long stagedContactTimestamp = 0;
    /**
     * Holds car data for the past time. Maps session time to a map of carId to
     * realtimeInfo.
     */
    private final Map<Integer, Map<Integer, RealtimeInfo>> history = new HashMap<>();
    /**
     * maximum time the history is saved for.
     */
    private final int HISTORY_MAX_TIME = 10000;

    /**
     * Get the instance of this extension.
     *
     * @return The instance.
     */
    public static ContactExtension getInstance() {
        if (instance == null) {
            instance = new ContactExtension();
        }
        return instance;
    }

    /**
     * Private constructor.
     */
    private ContactExtension() {
        EventBus.register(this);
        CLIENT = AccBroadcastingClient.getClient();
        REPLAY_EXTENSION = ReplayOffsetExtension.getInstance();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            if (event.getType() == BroadcastingEventType.ACCIDENT) {
                onAccident(event);
            }
        } else if (e instanceof RealtimeUpdateEvent) {
            commitStagedContact();
            updateHistory(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            saveInHistory(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof SessionChangedEvent) {
            history.clear();
        }
    }

    public void commitStagedContact() {
        // commit any staged contact that is older than 1 second.
        if (stagedContact != null) {
            long now = System.currentTimeMillis();
            if (now - stagedContactTimestamp > 1000) {
                commitAccident(stagedContact);
                stagedContact = null;
            }
        }
    }

    public void onAccident(BroadcastingEvent event) {
        // an accident event is usually 5000 ms after the contact.
        // to get an accurate timing we subtract that offset.
        int sessionTime = getSessionTimeFromHistory(
                CLIENT.getModel().getSessionInfo().getSessionTime() - 5000);
        CarInfo car = CLIENT.getModel().getCar(event.getCarId());

        // use realtime data from history
        car = car.withRealtime(history.get(sessionTime)
                .getOrDefault(event.getCarId(), car.getRealtime()));

        if (stagedContact != null) {
            stagedContact = stagedContact.withCar(sessionTime, car);
        } else {
            stagedContact = new ContactInfo(sessionTime,
                    REPLAY_EXTENSION.getReplayTimeFromSessionTime(sessionTime),
                    car,
                    CLIENT.getSessionId());
            stagedContactTimestamp = System.currentTimeMillis();
        }
    }

    private void commitAccident(ContactInfo contact) {
        if (contact.getCars().size() == 1) {
            contact = findOtherCars(contact);
        }

        String logMessage = String.format("Contact: %10s\t%s\t%s\t%d",
                contact.getCars().stream()
                        .map(car -> car.getCarNumberString())
                        .collect(Collectors.joining(", ")),
                TimeUtils.asDuration(contact.getSessionEarliestTime()),
                TimeUtils.asDuration(contact.getReplayTime()),
                contact.getSessionEarliestTime());
        LOG.info(logMessage);
        UILogger.log(logMessage);

        EventBus.publish(new ContactEvent(contact));
    }

    /**
     * Gets an exact session time from the history based on a requested time.
     *
     * @param requestedTime The requested time.
     * @return The exact time from history.
     */
    private int getSessionTimeFromHistory(int requestedTime) {
        if (history.containsKey(requestedTime)) {
            return requestedTime;
        }
        if (history.isEmpty()) {
            throw new IllegalArgumentException("History is empty");
        }
        int sessionTime = 0;
        int sdt = 999999;
        for (int t : history.keySet()) {
            int dt = Math.abs(t - requestedTime);
            if (dt < sdt) {
                sdt = dt;
                sessionTime = t;
            }
        }
        return sessionTime;
    }

    private void saveInHistory(RealtimeInfo info) {
        // add info to history.
        int sessionTime = CLIENT.getModel().getSessionInfo().getSessionTime();
        if (!history.containsKey(sessionTime)) {
            history.put(sessionTime, new HashMap<>());
        }
        history.get(sessionTime).put(info.getCarId(), info);
    }

    private void updateHistory(SessionInfo info) {
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

    private ContactInfo findOtherCars(ContactInfo contact) {
        // find history entry for the session time
        int time = getSessionTimeFromHistory(contact.getSessionEarliestTime());
        Map<Integer, RealtimeInfo> h = history.get(time);

        // find other car with the smallest distance
        int subjectId = contact.getCars().get(0).getCarId();
        RealtimeInfo subject = h.get(subjectId);
        Optional<CarInfo> closestCarO = h.values().stream()
                .filter(r -> r.getCarId() != subject.getCarId())
                .min((r1, r2) -> {
                    float d1 = Math.abs(getDistance(r1, subject));
                    float d2 = Math.abs(getDistance(r2, subject));
                    return ((Float) d1).compareTo(d2);
                })
                .map(r -> {
                    CarInfo car = CLIENT.getModel().getCar(r.getCarId());
                    return car.withRealtime(r);
                });
        
        if(closestCarO.isEmpty()){
            return contact;
        }
        
        CarInfo closestCar = closestCarO.get();
        // log 
        if (closestCar != null) {
            int trackMeters = CLIENT.getModel().getTrackInfo().getTrackMeters();
            float distance = (closestCar.getRealtime().getSplinePosition()
                    - subject.getSplinePosition()) * trackMeters;
            LOG.info(String.format("Contact: ?%s\t\t%.2fm\t%s",
                    closestCar.getCarNumberString(),
                    distance,
                    TimeUtils.asDuration(time)
            ));
        }
        
        return contact.withCar(
                contact.getSessionLatestTime(),
                closestCar);
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
