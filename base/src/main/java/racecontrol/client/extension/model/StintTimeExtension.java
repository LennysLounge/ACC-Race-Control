/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.ClientExtension;
import racecontrol.client.protocol.BroadcastingEvent;
import racecontrol.client.protocol.RealtimeInfo;
import static racecontrol.client.protocol.enums.BroadcastingEventType.PENALTYCOMMMSG;
import racecontrol.client.protocol.enums.CarLocation;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import static racecontrol.client.protocol.enums.SessionPhase.SESSION;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class StintTimeExtension
        extends ClientExtension
        implements EventListener {

    private static final Logger LOG = Logger.getLogger(StintTimeExtension.class.getName());
    /**
     * Maps carId's to the timestamp for when their stint timer starts.
     */
    private final Map<Integer, Long> stintStartTimestamp = new HashMap<>();
    /**
     * Maps carId's to their location of the previous update.
     */
    private final Map<Integer, CarLocation> prevCarLocation = new HashMap<>();
    /**
     * Holds carId's that served a penalty in the pits.
     */
    private final List<Integer> servedPenalty = new ArrayList<>();

    public StintTimeExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged((SessionPhaseChangedEvent) e);
        } else if (e instanceof RealtimeCarUpdateEvent) {
            carUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof BroadcastingEventEvent) {
            broadcastingEvent(((BroadcastingEventEvent) e).getEvent());
        }
    }

    private void sessionPhaseChanged(SessionPhaseChangedEvent event) {
        // reset all stint timers, set timestamps and set accurate flag.
        if (event.getSessionInfo().getPhase() == SESSION) {
            long now = System.currentTimeMillis();
            getWritableModel().cars.values().forEach(car -> {
                stintStartTimestamp.put(car.id, now);
                car.driverStintTime = 0;
                car.driverStintTimeAccurate = !event.isInitialisation();
            });
        }
    }

    private void carUpdate(RealtimeInfo info) {
        Car car = getWritableModel().cars.get(info.getCarId());
        long now = System.currentTimeMillis();
        if (!stintStartTimestamp.containsKey(car.id)) {
            stintStartTimestamp.put(car.id, now);
            car.driverStintTimeAccurate = false;
        }
        if (!prevCarLocation.containsKey(info.getCarId())) {
            prevCarLocation.put(info.getCarId(), info.getLocation());
        }

        if (info.getLocation() != PITLANE) {

            // reset stint time when exiting pit lane
            if (prevCarLocation.get(info.getCarId()) == PITLANE) {
                // only reset if we didnt serve a penalty.
                if (!servedPenalty.contains(info.getCarId())) {
                    stintStartTimestamp.put(info.getCarId(), now);
                    car.driverStintTimeAccurate = true;
                } else {
                    servedPenalty.remove(servedPenalty.indexOf(info.getCarId()));
                }
            }
            // set stint time
            car.driverStintTime = (int) (now - stintStartTimestamp.get(car.id));
        }

        // save car location.
        prevCarLocation.put(info.getCarId(), info.getLocation());
    }

    private void broadcastingEvent(BroadcastingEvent event) {
        if (event.getType() == PENALTYCOMMMSG) {
            Car car = getClient().getModel().cars.get(event.getCarId());
            LOG.info(event.getMessage()
                    + "\t" + car.carNumberString()
                    + "\t" + TimeUtils.asDurationShort(car.driverStintTime)
                    + "\t" + car.carLocation
            );
            if (!servedPenalty.contains(event.getCarId())) {
                servedPenalty.add(event.getCarId());
            }
        }
    }
}
