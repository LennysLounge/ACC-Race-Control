/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.extension.dangerdetection.YellowFlagEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.LAP_COUNT;
import static racecontrol.client.extension.statistics.CarProperties.SESSION_FINISHED;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.client.extension.statistics.StatisticsExtension;

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
     * Reference to the google sheets api extension.
     */
    private final GoogleSheetsAPIExtension GOOGLE_SHEETS_EXTENSION;
    /**
     * Reference to the statistics extension.
     */
    private final StatisticsExtension STATISTICS_EXTENSION;
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
    private final List<YellowFlagContactInfo> yellowEvents = new ArrayList<>();
    /**
     * Threshold for whether or not a yellof flag event triggers a possible
     * contact.
     */
    private final float YELLOW_FLAG_DISTANCE_THRESHOLD = 10f;

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
        GOOGLE_SHEETS_EXTENSION = GoogleSheetsAPIExtension.getInstance();
        STATISTICS_EXTENSION = StatisticsExtension.getInstance();
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
            removeOldYellowEvents(((RealtimeUpdateEvent) e).getSessionInfo());
            removeOldHistory(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            saveInHistory(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof YellowFlagEvent) {
            saveYellowFlagEvent((YellowFlagEvent) e);
        } else if (e instanceof SessionChangedEvent) {
            history.clear();
            yellowEvents.clear();
        }
    }

    /**
     * Saves a RealtimeInfo in the history.
     *
     * @param info the RealtimeInfo to save.
     */
    private void saveInHistory(RealtimeInfo info) {
        // add info to history.
        int sessionTime = CLIENT.getModel().getSessionInfo().getSessionTime();
        if (!history.containsKey(sessionTime)) {
            history.put(sessionTime, new HashMap<>());
        }
        history.get(sessionTime).put(info.getCarId(), info);
    }

    /**
     * Removes history that goes beyond the limit.
     *
     * @param info SessionInfo for the current session.
     */
    private void removeOldHistory(SessionInfo info) {
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

    /**
     * Gets the closest time in the history based on some requested time.
     *
     * @param requestedTime The requested time.
     * @return The closest match in the history.
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

    /*
    ////////////////////////////////////////////////////////////////////////////
    //      CONTACT EVENTS                                                    //
    ////////////////////////////////////////////////////////////////////////////
     */
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

    public void commitStagedContact() {
        // commit any staged contact that is older than 1 second.
        if (stagedContact != null) {
            long now = System.currentTimeMillis();
            if (now - stagedContactTimestamp > 1000) {
                commitContact(stagedContact);
                stagedContact = null;
            }
        }
    }

    private void commitContact(ContactInfo contact) {
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
        publishToSpreadsheet(contact);
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
            YellowFlagContactInfo yellow = iter.next();
            int dt = yellow.getSessionTime() - time;
            if (dt > -1000
                    && dt < 5000
                    && carsInvolved.containsKey(yellow.getFlaggedCar().getCarId())) {

                // removed matched event from list
                iter.remove();
                yellowFlaggedCars.add(yellow.getFlaggedCar().getCarId());
                LOG.info("\tmatched flag nr." + yellow.getYellowFlagEventId()
                        + "\t" + yellow.getFlaggedCar().getCarNumberString()
                        + "\t" + TimeUtils.asDelta(dt) + "s"
                );
                logYellowFlagContactInfo(yellow);
            }
        }

        return info.withYellowFlaggedCars(yellowFlaggedCars);
    }

    private void publishToSpreadsheet(ContactInfo info) {
        String cars = info.isGameContact() ? "" : "MAYBE\n";
        cars += info.getCars().stream()
                .map(car -> {
                    CarStatistics stats = STATISTICS_EXTENSION.getCar(car.getCarId());
                    String carNumber = String.valueOf(car.getCarNumber());
                    String lap = String.valueOf(stats.get(SESSION_FINISHED) ? "F" : (stats.get(LAP_COUNT) + 1));
                    String spin = info.getYellowFlaggedCars().contains(car.getCarId()) ? "Spin" : "";
                    return String.format("%s[%s] %s",
                            carNumber,
                            lap,
                            spin);
                })
                .collect(Collectors.joining("\n"));
        GOOGLE_SHEETS_EXTENSION.sendIncident(info.getSessionEarliestTime(), cars);
    }

    /*
    ////////////////////////////////////////////////////////////////////////////
    //  YELLOW FLAG EVENTS                                                    //
    ////////////////////////////////////////////////////////////////////////////
     */
    private void saveYellowFlagEvent(YellowFlagEvent event) {
        CarInfo flaggedCar = event.getCar();

        // find closest car at the moment the yellow flag was shown.
        Optional<CarInfo> closestCarInstant
                = CLIENT.getModel().getCarsInfo().values().stream()
                        .filter(car -> car.getCarId() != flaggedCar.getCarId())
                        .filter(car -> car.getRealtime().getLocation() != CarLocation.NONE)
                        .filter(car -> car.getRealtime().getLocation() != CarLocation.PITLANE)
                        .min((c1, c2) -> {
                            float d1 = Math.abs(getDistance(c1.getRealtime(), flaggedCar.getRealtime()));
                            float d2 = Math.abs(getDistance(c2.getRealtime(), flaggedCar.getRealtime()));
                            return Float.compare(d1, d2);
                        });

        // no closed car found
        if (closestCarInstant.isEmpty()) {
            return;
        }

        yellowEvents.add(new YellowFlagContactInfo(
                flaggedCar,
                closestCarInstant.get(),
                event.getSessionTime(),
                event.getId())
        );
    }

    private void removeOldYellowEvents(SessionInfo info) {
        // find yellow event that is to old and should be removed.
        List<YellowFlagContactInfo> oldYellowEvents = yellowEvents.stream()
                .filter(event -> info.getSessionTime() - event.getSessionTime() > HISTORY_MAX_TIME)
                .collect(Collectors.toList());

        // if an event is found, remove it, commit it and try search again.
        for (var oldEvent : oldYellowEvents) {
            if (yellowEvents.contains(oldEvent)) {
                if (isPossibleContact(oldEvent)) {
                    commitYellowFlagContact(oldEvent);
                } else {
                    yellowEvents.remove(oldEvent);
                }
            }
        }
    }

    private boolean isPossibleContact(YellowFlagContactInfo info) {
        if (info.getFlaggedCar().getRealtime().getLaps() < 1) {
            return false;
        }

        float distance = getDistance(info.getClosestCar().getRealtime(),
                info.getFlaggedCar().getRealtime()) * CLIENT.getModel().getTrackInfo().getTrackMeters();
        if (Math.abs(distance) > YELLOW_FLAG_DISTANCE_THRESHOLD) {
            return false;
        }

        return true;
    }

    private void logYellowFlagContactInfo(YellowFlagContactInfo info) {
        int trackMeters = CLIENT.getModel().getTrackInfo().getTrackMeters();
        LOG.info(String.format("\t\t%s\t%.2fm",
                info.getClosestCar().getCarNumberString(),
                getDistance(info.getClosestCar().getRealtime(),
                        info.getFlaggedCar().getRealtime()) * trackMeters
        ));
    }

    private void commitYellowFlagContact(YellowFlagContactInfo info) {
        ContactInfo contact = new ContactInfo(
                info.getSessionTime(),
                REPLAY_EXTENSION.getReplayTimeFromSessionTime(info.getSessionTime()),
                CLIENT.getSessionId())
                .withCar(info.getSessionTime(), info.getFlaggedCar())
                .withCar(info.getSessionTime(), info.getClosestCar())
                .withYellowFlaggedCars(Arrays.asList(info.getFlaggedCar().getCarId()))
                .withIsGameContact(false);

        LOG.info(String.format("Possible contact for yellow nr.%s",
                info.getYellowFlagEventId()));
        LOG.info(String.format("\t\t%s\t-", info.getFlaggedCar().getCarNumberString()));
        logYellowFlagContactInfo(info);
        commitContact(contact);
    }

    /**
     * Finds the spline distance between two cars.
     *
     * @param r1 RealtimeInfo for car 1.
     * @param r2 RealtimeInfo for car 2.
     * @return The spline distance between two cars.
     */
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
