/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import java.util.List;
import java.util.stream.Collectors;
import racecontrol.client.ClientExtension;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.CarStatistics;
import static racecontrol.client.extension.statistics.CarStatistics.REALTIME_POSITION;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 * An extension for calculating gaps to other cars.
 *
 * @author Leonard
 */
public class GapExtension
        extends ClientExtension
        implements EventListener {

    /**
     * Current track data.
     */
    private TrackData trackData;
    /**
     * Gap calculator.
     */
    private final GapCalculator gapCalculator = new GapCalculator();

    public GapExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        } else if (e instanceof TrackDataEvent) {
            trackData = ((TrackDataEvent) e).getTrackData();
            gapCalculator.setTrackData(trackData);
        }
    }

    public void onSessionUpdate(SessionInfo info) {
        //calculate gap to session best lap time.
        for (Car car : getWritableModel().cars.values()) {
            if (!car.connected) {
                continue;
            }
            car.deltaToSessionBest = car.bestLap.getLapTimeMS()
                    - info.getBestSessionLap().getLapTimeMS();
        }

        // calculate gaps to position ahead/behind and leader.
        List<Car> cars = getWritableModel().cars.values().stream()
                .filter(car -> car.connected)
                .sorted((c1, c2) -> {
                    CarStatistics car1 = StatisticsExtension.getInstance().getCar(c1.id);
                    CarStatistics car2 = StatisticsExtension.getInstance().getCar(c2.id);
                    return car1.get(REALTIME_POSITION).compareTo(car2.get(REALTIME_POSITION));
                })
                .collect(Collectors.toList());

        for (int i = 0; i < cars.size(); i++) {
            Car subject = cars.get(i);
            if (i == 0) {
                subject.carPositionAhead = 0;
                subject.gapPositionAhead = Integer.MAX_VALUE;
            } else {
                subject.carPositionAhead = cars.get(i - 1).id;
                subject.gapPositionAhead = (int) gapCalculator
                        .calculateGap(subject, cars.get(i - 1));
            }
            if (i == cars.size() - 1) {
                subject.carPositionBehind = 0;
                subject.gapPositionBehind = Integer.MAX_VALUE;
            } else {
                subject.carPositionBehind = cars.get(i + 1).id;
                subject.gapPositionBehind = (int) gapCalculator
                        .calculateGap(cars.get(i + 1), subject);
            }
            subject.gapToLeader = (int) gapCalculator
                    .calculateGap(subject, cars.get(0));
            subject.lapsBehindLeader = cars.get(0).lapCount
                    + cars.get(0).splinePosition
                    - subject.lapCount
                    - subject.splinePosition;
        }

        // calculate gaps to car ahead and behind.
        // cap to cars ahead / behind
        cars = getWritableModel().cars.values().stream()
                .filter(car -> car.connected)
                .sorted((c1, c2) -> {
                    return Float.compare(c1.splinePosition, c2.splinePosition);
                })
                .collect(Collectors.toList());
        for (int i = 0; i < cars.size(); i++) {
            int ahead = (i + 1) % cars.size();
            int behind = (i == 0) ? cars.size() - 1 : i - 1;
            Car subject = cars.get(i);
            subject.carAhead = cars.get(ahead).id;
            subject.carBehind = cars.get(behind).id;
            subject.gapAhead = (int) gapCalculator.calculateGap(subject, cars.get(ahead));
            subject.gapBehind = (int) gapCalculator.calculateGap(cars.get(behind), subject);
        }
    }
}
