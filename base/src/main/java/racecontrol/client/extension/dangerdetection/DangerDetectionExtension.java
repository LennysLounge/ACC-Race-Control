/*
 * Copyright (c) 2021 Leonard Sch�ngel
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
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.enums.CarLocation;
import static racecontrol.client.data.enums.CarLocation.PITENTRY;
import static racecontrol.client.data.enums.CarLocation.PITEXIT;
import static racecontrol.client.data.enums.CarLocation.PITLANE;
import racecontrol.client.data.enums.SessionPhase;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.utility.TimeUtils;

/**
 * Find the correct flag for a car.
 *
 * @author Leonard
 */
public class DangerDetectionExtension extends ClientExtension
        implements EventListener {

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
     * How far to look ahead when calculating the speed tolerances.
     */
    public final float SPEED_LOOKAHEAD_COUNT = 3;
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
     * Map for velocity tolerances for white flags.
     */
    private List<Float> velocityToleranceWhiteMap;
    /**
     * Map for velocity tolerances for yellow flags.
     */
    private List<Float> velocityToleranceYellowMap;
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
     * Holds cars that are protected by the pit exit. Maps carId to speed.
     */
    private final Map<Integer, Integer> pitExitProtection = new HashMap<>();
    /**
     * Id counter for yellow flag events.
     */
    private int idCounter = 0;

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
            doPitExitProtection(((RealtimeCarUpdateEvent) e).getInfo());
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

    public List<Float> getVelocityToleranceWhiteMap() {
        return new ArrayList<>(velocityToleranceWhiteMap);
    }

    public List<Float> getVelocityToleranceYellowMap() {
        return new ArrayList<>(velocityToleranceYellowMap);
    }

    private void onTrackData(TrackData data) {
        velocityMap = data.getGt3VelocityMap();
        velocityToleranceWhiteMap = new ArrayList<>();
        velocityToleranceYellowMap = new ArrayList<>();
        directionToleranceMap = new ArrayList<>();
        for (int i = 0; i < velocityMap.size(); i++) {
            float v = velocityMap.get(i);
            float z = 1 - (v - 50) / 200;
            z = DIR_BASE_TOLERANCE + Math.max(0, Math.min(1, z * z));
            directionToleranceMap.add(z);

            float smallestV = 1000;
            for (int j = 0; j < SPEED_LOOKAHEAD_COUNT; j++) {
                int index = (i + j) % velocityMap.size();
                smallestV = Math.min(smallestV, velocityMap.get(index));
            }
            velocityToleranceWhiteMap.add(smallestV
                    - Math.min(SPEED_WHITE_TOLERANCE, smallestV * 0.5f));
            velocityToleranceYellowMap.add(smallestV
                    - Math.min(SPEED_YELLOW_TOLERANCE, smallestV * 0.6f));
        }
        directionMap = data.getDirectionMap();
        hasTrackData = true;
    }

    /**
     * Tests if a car is protected from flags by exiting the pits. Aslong as the
     * speed of a car exiting the pits is increasing it is protected from flags.
     * The first time the car slows down this protection is removed.
     *
     * @param info the cars realtime info.
     */
    private void doPitExitProtection(RealtimeInfo info) {
        if (info.getLocation() == PITLANE
                || info.getLocation() == PITEXIT
                || info.getLocation() == PITENTRY) {
            pitExitProtection.put(info.getCarId(), info.getKMH());
            return;
        }
        if (pitExitProtection.containsKey(info.getCarId())) {
            if (info.getKMH() < pitExitProtection.get(info.getCarId())) {
                pitExitProtection.remove(info.getCarId());
            } else {
                pitExitProtection.put(info.getCarId(), info.getKMH());
            }
        }
    }

    private void testTolerances(RealtimeInfo info) {
        if (!shouldProcessData(info)) {
            return;
        }

        float vDiff = info.getKMH() - getValueFromMap(velocityMap, info.getSplinePosition());
        float dDiff = Math.abs(angleBetewen(info.getYaw(), getDMapValue(info.getSplinePosition())));

        float vTolerance = getValueFromMap(velocityToleranceWhiteMap, info.getSplinePosition());
        if (info.getKMH() < vTolerance) {
            float actualV = getValueFromMap(velocityMap, info.getSplinePosition());
            setWhiteFlag(info.getCarId(), vDiff, vTolerance - actualV);
        }

        vTolerance = getValueFromMap(velocityToleranceYellowMap, info.getSplinePosition());
        float dTolerance = getValueFromMap(directionToleranceMap, info.getSplinePosition());
        if (dDiff > dTolerance || info.getKMH() < vTolerance) {
            setYellowFlag(info.getCarId(), info.getKMH() < vTolerance, dDiff > dTolerance);
        }
    }

    private boolean shouldProcessData(RealtimeInfo info) {
        if (!hasTrackData) {
            return false;
        }
        if (velocityMap.size() == 0) {
            return false;
        }
        if (info.getLocation() != CarLocation.TRACK) {
            return false;
        }
        SessionInfo sessionInfo = getWritableModel().session.raw;
        if (sessionInfo.getPhase() != SessionPhase.SESSION
                && sessionInfo.getPhase() != SessionPhase.SESSIONOVER) {
            return false;
        }
        if (sessionInfo.getSessionTime() < 15000) {
            return false;
        }
        if (pitExitProtection.containsKey(info.getCarId())) {
            return false;
        }
        return true;
    }

    private void setWhiteFlag(int carId, float vDiff, float tolerance) {
        /*
        if (!whiteFlaggedCars.containsKey(carId)) {
            String carNumber = AccBroadcastingClient.getClient().getModel().getCar(carId).getCarNumber() + "";
            LOG.info("White Flag for: #" + carNumber
                    + String.format(", speed: %.2f", vDiff)
                    + String.format(", t: %.2f", tolerance)
            );

        }
         */
        long now = System.currentTimeMillis();
        whiteFlaggedCars.put(carId, now);
    }

    private void setYellowFlag(int carId, boolean isSlow, boolean isSpin) {
        boolean isNew = !yellowFlaggedCars.containsKey(carId);
        long now = System.currentTimeMillis();
        yellowFlaggedCars.put(carId, now);

        // yellow flag overrides a white flag.
        whiteFlaggedCars.remove(carId);

        if (isNew) {
            if (isSpin) {
                SessionInfo info = getWritableModel().session.raw;
                int sessionTime = info.getSessionTime();
                int replayTime = ReplayOffsetExtension.getInstance().getReplayTimeFromSessionTime(sessionTime);
                String logMessage = "Yellow Flag nr." + idCounter + " :"
                        + client.getBroadcastingData().getCar(carId).getCarNumberString()
                        + "\t" + TimeUtils.asDuration(sessionTime)
                        + "\t" + TimeUtils.asDuration(replayTime)
                        + "\t";
                logMessage += isSlow ? "[Slow]" : "";
                logMessage += isSpin ? "[Spin]" : "";

                LOG.info(logMessage);
                EventBus.publish(new YellowFlagEvent(
                        client.getBroadcastingData().getCar(carId),
                        sessionTime,
                        idCounter++
                ));
            }
        }
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
        if (map.isEmpty()) {
            return 0;
        }
        int size = map.size();
        int lowerIndex = (int) Math.floor(position * size) % size;
        int upperIndex = (lowerIndex + 1) % size;
        float t = (position * size) % 1;
        float lower = map.get(lowerIndex);
        float upper = map.get(upperIndex);
        return lower * (1 - t) + upper * t;
    }

    private float getDMapValue(float position) {
        if (directionMap.isEmpty()) {
            return 0;
        }
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
