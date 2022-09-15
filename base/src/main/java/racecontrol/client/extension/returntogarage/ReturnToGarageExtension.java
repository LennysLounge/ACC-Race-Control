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
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.model.Car;
import racecontrol.client.protocol.RealtimeInfo;
import racecontrol.client.protocol.enums.CarLocation;
import static racecontrol.client.protocol.enums.CarLocation.PITEXIT;
import static racecontrol.client.protocol.enums.CarLocation.PITLANE;
import static racecontrol.client.protocol.enums.CarLocation.TRACK;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.RTG_ENABLED;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class ReturnToGarageExtension
        extends ClientExtension {

    /**
     * Singelton instance.
     */
    private static ReturnToGarageExtension instance;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(ReturnToGarageExtension.class.getName());
    /**
     * Wether or not the extension is enabled.
     */
    private boolean enabled;
    /**
     * A map of CarId to their previous car location.
     */
    private final Map<Integer, CarLocation> carLocations = new HashMap<>();

    /**
     * Get the instance of this extension.
     *
     * @return the instance of this extension
     */
    public static ReturnToGarageExtension getInstance() {
        if (instance == null) {
            instance = new ReturnToGarageExtension();
        }
        return instance;
    }

    /**
     * Private constructor.
     */
    private ReturnToGarageExtension() {
        setEnabled(PersistantConfig.get(RTG_ENABLED));
    }

    /**
     * Set the extension enabled or not.
     *
     * @param enabled wheter the extension is enabled or not
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        carLocations.clear();
        PersistantConfig.put(RTG_ENABLED, enabled);
        EventBus.publish(new ReturnToGarageEnabledEvent(enabled));
    }

    /**
     * Return true if the extension is enabled.
     *
     * @return Return true if the extension is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpenedEvent) {
            carLocations.clear();
        } else if (e instanceof CarConnectedEvent) {
            Car car = ((CarConnectedEvent) e).getCar();
            carLocations.put(car.id, car.carLocation);
        } else if (e instanceof CarDisconnectedEvent) {
            Car car = ((CarDisconnectedEvent) e).getCar();
            carLocations.remove(car.id);
        } else if (e instanceof RealtimeCarUpdateEvent) {
            if (enabled) {
                onRealtimeUpdate(((RealtimeCarUpdateEvent) e).getInfo());
            }
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
            // push to google sheet
            GoogleSheetsAPIExtension.getInstance().sendIncident(
                    sessionTime,
                    String.valueOf(car.carNumber) + " RTG"
            );
        }
        carLocations.put(info.getCarId(), info.getLocation());
    }

}
