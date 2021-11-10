/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.dangerdetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import processing.core.PVector;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.ClientExtension;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.enums.CarLocation;
import racecontrol.client.data.enums.SessionPhase;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.statistics.CarProperties;
import racecontrol.client.extension.statistics.StatisticsExtension;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
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
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;
    /**
     * Base tolerance for the direction.
     */
    public final float DIR_BASE_TOLERANCE = 0.2f;
    /**
     * Tolerance for the speed before a white flag is shown.
     */
    public final float SPEED_WHITE_TOLERANCE = 60;
    /**
     * Tolerance for the speed before a yellow flag is shown.
     */
    public final float SPEED_YELLOW_TOLERANCE = 100;
    /**
     * Time required without incident to remove the flag status.
     */
    private final int FLAG_REMOVE_TIME = 2000;
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
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
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
        return yellowFlaggedCars.containsKey(carId);
    }

    public List<Float> getDirectionToleranceMap() {
        return new ArrayList<>(directionToleranceMap);
    }

    private void onTrackData(TrackData data) {
        velocityMap = data.getGt3VelocityMap();
        directionToleranceMap = new ArrayList<>();
        for (int i = 0; i < velocityMap.size(); i++) {
            float z = 1 - (velocityMap.get(i) - 50) / 200;
            z = DIR_BASE_TOLERANCE + Math.max(0, Math.min(1, z * z));
            directionToleranceMap.add(z);
        }
        directionMap = data.getDirectionMap();
        hasTrackData = true;
    }

    private void testTolerances(RealtimeInfo info) {
        if (!shouldProcessData(info)) {
            return;
        }

        float vDiff = info.getKMH() - getValueFromMap(velocityMap, info.getSplinePosition());
        float dDiff = Math.abs(angleBetewen(info.getYaw(), getDMapValue(info.getSplinePosition())));

        if (vDiff < -SPEED_WHITE_TOLERANCE) {
            setWhiteFlag(info.getCarId(), vDiff);
        }
        float dTolerance = getValueFromMap(directionToleranceMap, info.getSplinePosition());
        if (vDiff < -SPEED_WHITE_TOLERANCE * 2 || dDiff > dTolerance) {
            setYellowFlag(info.getCarId(), vDiff, dDiff);
        }
    }

    private boolean shouldProcessData(RealtimeInfo info) {
        if (!hasTrackData) {
            return false;
        }
        if (info.getLocation() != CarLocation.TRACK) {
            return false;
        }
        if (client.getModel().getSessionInfo().getPhase() != SessionPhase.SESSION
                && client.getModel().getSessionInfo().getPhase() != SessionPhase.SESSIONOVER) {
            return false;
        }
        return true;
    }

    private void setWhiteFlag(int carId, float vDiff) {
        if (!whiteFlaggedCars.containsKey(carId)) {
            String carNumber = AccBroadcastingClient.getClient().getModel().getCar(carId).getCarNumber() + "";
            LOG.info("White Flag for: " + carNumber
                    + ", speed: " + vDiff);
        }
        long now = System.currentTimeMillis();
        whiteFlaggedCars.put(carId, now);
    }

    private void setYellowFlag(int carId, float vDiff, float dDiff) {
        if (!yellowFlaggedCars.containsKey(carId)) {
            String carNumber = AccBroadcastingClient.getClient().getModel().getCar(carId).getCarNumber() + "";
            LOG.info("Yellow Flag for: " + carNumber
                    + ", speed: " + vDiff
                    + ", angle: " + dDiff);
            AccBroadcastingClient.getClient().sendChangeFocusRequest(carId);
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

    private float angleBetewen(float a1, float a2) {
        float diff = a1 - a2;
        if (diff < -Math.PI) {
            diff += Math.PI * 2;
        }
        if (diff > Math.PI) {
            diff -= Math.PI * 2;
        }
        return diff;
    }

}
