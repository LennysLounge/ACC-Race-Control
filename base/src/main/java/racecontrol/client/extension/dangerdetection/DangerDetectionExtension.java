/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.dangerdetection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import processing.core.PVector;
import racecontrol.client.ClientExtension;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;

/**
 * Find the correct flag for a car.
 *
 * @author Leonard
 */
public class DangerDetectionExtension
        implements EventListener, ClientExtension {

    /**
     * Singelton instance.
     */
    private static DangerDetectionExtension instance;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(DangerDetectionExtension.class.getName());
    /**
     * Base tolerance for the direction.
     */
    private final float DIR_BASE_TOLERANCE = 0.2f;
    /**
     * Tolerance for the speed.
     */
    private final float SPEED_BASE_TOLERANCE = 50;
    /**
     * Time required without incident to remove the flag status.
     */
    private final int FLAG_REMOVE_TIME = 5000;
    /**
     * Velocity map that maps a point on track to its nominal velocity.
     */
    private List<Float> velocityMap;
    /**
     * Direction map that maps a point on track to its nominal direction.
     */
    private List<Float> directionMap;
    /**
     * Map for the direction tolerance over the lap.
     */
    private List<Float> directionToleranceMap;
    /**
     * Flag for when track data is present.
     */
    private boolean hasTrackData = false;
    /**
     * Holds cars that are white flagged. maps carId to timestamp.
     */
    private final Map<Integer, Long> whiteFlaggedCars = new HashMap<>();
    /**
     * Holds cars that are yellow flagged. maps carId to timestamp.
     */
    private final Map<Integer, Long> yellowFlaggedCars = new HashMap<>();

    /**
     * Get singelton instance.
     *
     * @return Singelton instance.
     */
    public static DangerDetectionExtension getInstance() {
        if (instance == null) {
            instance = new DangerDetectionExtension();
        }
        return instance;
    }

    private DangerDetectionExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackDataEvent) {
            onTrackData(((TrackDataEvent) e).getTrackData());
        } else if (e instanceof RealtimeCarUpdateEvent) {
            testTolerances(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof RealtimeUpdateEvent) {
            removeFlags();
        }
    }

    /**
     * Returns true if the car is white flagged.
     *
     * @param carId the car to test.
     * @return true if the car is white flagged.
     */
    public boolean isCarWhiteFlag(int carId) {
        return whiteFlaggedCars.containsKey(carId);
    }

    /**
     * Returns true if the car is yellow flagged.
     *
     * @param carId the car to test.
     * @return true if the car is white flagged.
     */
    public boolean isCarYellowFlag(int carId) {
        return whiteFlaggedCars.containsKey(carId);
    }

    private void onTrackData(TrackData data) {
        velocityMap = data.getGt3VelocityMap();
        for (int i = 0; i < velocityMap.size(); i++) {
            float z = 1 - (velocityMap.get(i) - 50) / 200;
            z = DIR_BASE_TOLERANCE + Math.max(0, Math.min(1, z * z));
            directionToleranceMap.add(z);
        }
        directionMap = data.getDirectionMap();
        hasTrackData = true;
    }

    private void testTolerances(RealtimeInfo info) {
        if (!hasTrackData) {
            return;
        }
        float vDiff = info.getKMH() - getValueFromMap(velocityMap, info.getSplinePosition());
        float dDiff = Math.abs(info.getYaw() - getDMapValue(info.getSplinePosition()));

        if (vDiff < -SPEED_BASE_TOLERANCE) {
            setWhiteFlag(info.getCarId(), vDiff);
        }
        float dTolerance = getValueFromMap(directionToleranceMap, info.getSplinePosition());
        if (vDiff < -SPEED_BASE_TOLERANCE * 2 || dDiff > dTolerance) {
            setYellowFlag(info.getCarId(), vDiff, dDiff);
        }
    }

    private void setWhiteFlag(int carId, float vDiff) {
        if (!whiteFlaggedCars.containsKey(carId)) {
            LOG.info("White Flag for: " + carId
                    + ", speed: " + vDiff);
        }
        long now = System.currentTimeMillis();
        whiteFlaggedCars.put(carId, now);
    }

    private void setYellowFlag(int carId, float vDiff, float dDiff) {
        if (!whiteFlaggedCars.containsKey(carId)) {
            LOG.info("Yellow Flag for: " + carId
                    + ", speed: " + vDiff
                    + ", angle: " + dDiff);
        }
        long now = System.currentTimeMillis();
        yellowFlaggedCars.put(carId, now);
    }

    private void removeFlags() {
        long now = System.currentTimeMillis();
        var iter = whiteFlaggedCars.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, Long> entry = iter.next();
            if (now - entry.getValue() > FLAG_REMOVE_TIME) {
                iter.remove();
            }
        }
        iter = yellowFlaggedCars.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, Long> entry = iter.next();
            if (now - entry.getValue() > FLAG_REMOVE_TIME) {
                iter.remove();
            }
        }
    }

    private float getValueFromMap(List<Float> map, float position) {
        int size = map.size();
        int lowerIndex = (int) Math.floor(position * size) % size;
        int upperIndex = (lowerIndex + 1) % size;
        float t = (position * size) % 1;
        float lower = map.get(lowerIndex);
        float upper = map.get(upperIndex);
        return lower * (1 - t) + upper * t;
    }

    private float getDMapValue(float position) {
        int size = directionMap.size();
        int lowerIndex = (int) Math.floor(position * size) % size;
        int upperIndex = (lowerIndex + 1) % size;
        float t = (position * size) % 1;
        PVector lower = PVector.fromAngle(directionMap.get(lowerIndex)).mult(1 - t);
        PVector upper = PVector.fromAngle(directionMap.get(upperIndex)).mult(t);
        return lower.add(upper).heading();
    }

}
