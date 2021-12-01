/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
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
     * Holds current yellow flag events.
     */
    private final List<YellowFlagEvent> yellowEvents = new ArrayList<>();

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
            updateYellowEvents(((RealtimeUpdateEvent) e).getSessionInfo());
            updateHistory(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            saveInHistory(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof SessionChangedEvent) {
            history.clear();
            yellowEvents.clear();
        } else if (e instanceof YellowFlagEvent) {
            yellowEvents.add((YellowFlagEvent) e);
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

        contact = matchYellowEvents(contact);

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

        if (closestCarO.isEmpty()) {
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

    private ContactInfo matchYellowEvents(ContactInfo info) {
        int time = getSessionTimeFromHistory(info.getSessionEarliestTime());

        Map<Integer, CarInfo> carsInvolved = info.getCars().stream()
                .collect(Collectors.toMap(
                        car -> car.getCarId(), car -> car
                ));

        // find yellow flags that are within range of this collision
        List<Integer> yellowFlaggedCars = new ArrayList<>();
        var iter = yellowEvents.iterator();
        while (iter.hasNext()) {
            YellowFlagEvent yellow = iter.next();
            int dt = yellow.getSessionTime() - time;
            if (dt > -1000
                    && dt < 5000
                    && carsInvolved.containsKey(yellow.getCar().getCarId())) {

                // removed matched event from list
                iter.remove();
                yellowFlaggedCars.add(yellow.getCar().getCarId());
                LOG.info("\tmatched flag nr." + yellow.getId()
                        + "\t" + TimeUtils.asDelta(dt)
                        + "\t" + yellow.getCar().getCarNumberString()
                );
                logYellowCandidates(yellow);
                logMatchedYellow(yellow, info);
            }
        }

        return info.withYellowFlaggedCars(yellowFlaggedCars);
    }

    private void updateYellowEvents(SessionInfo info) {
        // remove old yellow flag events
        var iter = yellowEvents.iterator();
        while (iter.hasNext()) {
            var event = iter.next();
            if (info.getSessionTime() - event.getSessionTime() > HISTORY_MAX_TIME) {
                logUnmatchedYellow(event);
                LOG.info("remoing yellow");
                logYellowCandidates(event);
                iter.remove();
            }
        }
    }

    private void logYellowCandidates(YellowFlagEvent event) {
        // find 3 closed cars to the yellow flagged car.
        int trackMeters = CLIENT.getModel().getTrackInfo().getTrackMeters();
        final RealtimeInfo flaggedCar = event.getCar().getRealtime();
        int time = getSessionTimeFromHistory(event.getSessionTime());

        Map<Integer, RealtimeInfo> moment = history.get(time);

        moment.values().stream()
                .filter(car -> car.getCarId() != flaggedCar.getCarId())
                .sorted((c1, c2) -> {
                    float f1 = Math.abs(carDistance(c1, flaggedCar));
                    float f2 = Math.abs(carDistance(c2, flaggedCar));
                    return Float.compare(f1, f2);
                })
                .limit(3)
                .forEach(car -> {
                    float distance = carDistance(car, flaggedCar);
                    CarInfo c = CLIENT.getModel().getCar(car.getCarId());
                    LOG.info(String.format("\t\t%s\t%.2fm",
                            c.getCarNumberString(),
                            distance * trackMeters));
                });
    }

    private float carDistance(RealtimeInfo car1, RealtimeInfo car2) {
        float distance = car1.getSplinePosition()
                - car2.getSplinePosition();
        if (distance > 0.5) {
            distance -= 1;
        }
        if (distance < -0.5) {
            distance += 1;
        }
        return distance;
    }

    private void logMatchedYellow(YellowFlagEvent event, ContactInfo info) {
        if (info.getCars().size() != 2) {
            return;
        }

        int time = getSessionTimeFromHistory(event.getSessionTime());
        int trackMeters = CLIENT.getModel().getTrackInfo().getTrackMeters();
        Map<Integer, RealtimeInfo> h = history.get(time);
        CarInfo flaggedCar = event.getCar();

        for (CarInfo car : info.getCars()) {
            if (car.getCarId() == flaggedCar.getCarId()) {
                continue;
            }

            // find distance to subject
            float distance = carDistance(
                    h.get(car.getCarId()),
                    flaggedCar.getRealtime()) * trackMeters;

            // find car closest to subject
            Optional<RealtimeInfo> closestOpt = h.values().stream()
                    .filter(c -> c.getCarId() != flaggedCar.getCarId())
                    .sorted((c1, c2) -> {
                        float f1 = Math.abs(carDistance(c1, flaggedCar.getRealtime()));
                        float f2 = Math.abs(carDistance(c2, flaggedCar.getRealtime()));
                        return Float.compare(f1, f2);
                    })
                    .findFirst();

            boolean closestIsContactOponent = false;
            if (closestOpt.isPresent()) {
                if (car.getCarId() == closestOpt.get().getCarId()) {
                    closestIsContactOponent = true;
                }
            }

            boolean isLapOne = flaggedCar.getRealtime().getLaps() == 0;

            try {
                FileWriter fw = new FileWriter("../../../matched.csv", true);
                fw.write(String.format("%.2f\t%d\t%d\n",
                        distance,
                        closestIsContactOponent ? 1 : 0,
                        isLapOne ? 1 : 0
                ));
                fw.flush();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ContactExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void logUnmatchedYellow(YellowFlagEvent event) {
        int time = getSessionTimeFromHistory(event.getSessionTime());
        int trackMeters = CLIENT.getModel().getTrackInfo().getTrackMeters();
        Map<Integer, RealtimeInfo> h = history.get(time);
        CarInfo flaggedCar = event.getCar();

        Optional<RealtimeInfo> closestCar = h.values().stream()
                .filter(car -> car.getCarId() != flaggedCar.getCarId())
                .sorted((c1, c2) -> {
                    float f1 = Math.abs(carDistance(c1, flaggedCar.getRealtime()));
                    float f2 = Math.abs(carDistance(c2, flaggedCar.getRealtime()));
                    return Float.compare(f1, f2);
                })
                .findFirst();

        if (closestCar.isPresent()) {
            float distance = carDistance(
                    h.get(closestCar.get().getCarId()),
                    flaggedCar.getRealtime()) * trackMeters; 
            
            boolean isLapOne = flaggedCar.getRealtime().getLaps() == 0;
            
            try {
                FileWriter fw = new FileWriter("../../../un-matched.csv", true);
                fw.write(String.format("%.2f\t%d\n",
                        distance,
                        isLapOne ? 1 : 0
                        ));
                fw.flush();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ContactExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
