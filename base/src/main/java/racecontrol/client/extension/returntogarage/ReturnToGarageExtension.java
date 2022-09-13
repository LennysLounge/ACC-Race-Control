/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.returntogarage;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.ClientExtension;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.CarDisconnectedEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.enums.CarLocation;
import static racecontrol.client.protocol.enums.CarLocation.PITEXIT;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import static racecontrol.client.protocol.enums.CarLocation.TRACK;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class ReturnToGarageExtension
        extends ClientExtension {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ReturnToGarageExtension.class.getName());

    /**
     * A map of CarId to their previous car location.
     */
    private final Map<Integer, CarLocation> carLocations = new HashMap<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof CarConnectedEvent) {
            Car car = ((CarConnectedEvent) e).getCar();
            carLocations.put(car.id, car.carLocation);
        } else if (e instanceof CarDisconnectedEvent) {
            Car car = ((CarDisconnectedEvent) e).getCar();
            carLocations.remove(car.id);
        } else if (e instanceof RealtimeCarUpdateEvent) {
            onRealtimeUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        }
    }

    private void onRealtimeUpdate(RealtimeInfo info) {
        Car car = getWritableModel().cars.get(info.getCarId());

        if (car.carLocation == PITLANE
                && (carLocations.get(car.id) == TRACK
                || carLocations.get(car.id) == PITEXIT)) {
            int sessionTime = getWritableModel().session.raw.getSessionTime();

            LOG.info("Car "
                    + car.carNumberString()
                    + " Returned to garage at "
                    + TimeUtils.asDuration(sessionTime)
            );

            EventBus.publish(new ReturnToGarageEvent(
                    getWritableModel().currentSessionId,
                    getWritableModel().session.raw.getSessionTime(),
                    ReplayOffsetExtension.getInstance()
                            .getReplayTimeFromSessionTime(sessionTime),
                    car
            ));
        }
        carLocations.put(info.getCarId(), info.getLocation());
    }

}
