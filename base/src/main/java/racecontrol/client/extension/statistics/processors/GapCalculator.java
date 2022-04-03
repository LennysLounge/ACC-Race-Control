/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import racecontrol.client.data.CarInfo;
import racecontrol.client.data.RealtimeInfo;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.model.Car;

/**
 *
 * @author Leonard
 */
public class GapCalculator {

    private static final Logger LOG = Logger.getLogger(GapCalculator.class.getName());

    /**
     * Predicted lap time.
     */
    private float lapTime;
    /**
     * Track data.
     */
    private TrackData trackData;

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
        lapTime = calculateLapTime();
    }

    public float getLapTime() {
        return lapTime;
    }

    /**
     * Calculates the gap from the car behind to the car infront.
     *
     * @param behind Car info of the car behind.
     * @param infront Car info of the car infront.
     * @return gap from -> to in ms.
     */
    public float calculateGap(Car behind, Car infront) {
        if (trackData.getGt3VelocityMap().isEmpty()) {
            return calculateGapNaive(behind, infront);
        }
        return calculateGap(behind.realtimeRaw.getSplinePosition(),
                infront.realtimeRaw.getSplinePosition());
    }

    public float calculateGap(float behind, float infront) {
        List<Float> velocityMap = trackData.getGt3VelocityMap();

        float start = behind;
        float end = infront;
        //make sure the end is always > than the start so we dont have to worry
        //about that later.
        //0->1 wrappeing does not matter because it will be Modulo'ed out when
        //accessing the velocity map.
        if (end < start) {
            end += 1;
        }

        float stepSize = 1f / velocityMap.size();
        float totalTime = 0;
        float currentPosition = start;
        //if there is a checkpoint inbetween the current point and the end
        //calculate the time to the checkpoint first.
        while (currentPosition + stepSize < end) {
            totalTime += calcTimeBetweenSplinePoints(currentPosition, currentPosition + stepSize);
            currentPosition += stepSize;
        }
        //add time to the end point.
        totalTime += calcTimeBetweenSplinePoints(currentPosition, end);
        return totalTime * 1000;
    }

    /**
     * Calculates the time it takes to drive from spline position s1 to s2 based
     * on the velocity map.
     *
     * @param s1 start spline position.
     * @param s2 end spline position.
     * @return time in ms.
     */
    private float calcTimeBetweenSplinePoints(float s1, float s2) {
        float s1Velocity = findVmapVelocityForPosition(s1);
        float s2Velocity = findVmapVelocityForPosition(s2);
        float distance = (s2 - s1) * trackData.getTrackMeters();
        return calcTimeBetweenPointsWithVelocity(s1Velocity, s2Velocity, distance);
    }

    /**
     * Find the corosponding vmap velocity for the specified position by
     * interpolating between the two closest vMap points.
     *
     * @param s the position.
     * @return the vMap speed.
     */
    private float findVmapVelocityForPosition(float s) {
        List<Float> velocityMap = trackData.getGt3VelocityMap();
        int lowerIndex = (int) Math.floor(s * velocityMap.size()) % velocityMap.size();
        int upperIndex = (lowerIndex + 1) % velocityMap.size();
        float t = s * velocityMap.size() % 1;
        float rtn = (velocityMap.get(lowerIndex) * (1 - t) + velocityMap.get(upperIndex) * t) / 3.6f;
        return rtn;
    }

    /**
     * Calculates the time it takes a car to accelerate from v0 to v1 in the
     * distance d.
     *
     * @param v0 start velocity in m/s.
     * @param v1 end velocity in m/s.
     * @param d distance in m.
     * @return time in ms to accelerate from v0 to v1 in d meter.
     */
    private float calcTimeBetweenPointsWithVelocity(float v0, float v1, float d) {
        if (v0 + v1 == 0) {
            return 0;
        }
        float rtn = 2 * d / (v0 + v1);
        return rtn;
    }

    /**
     * Calculates the gap from the car behind to the car infront in a naive and
     * unprecice way.
     *
     * @param behind Car info of the car behind.
     * @param infront Car info of the car infront.
     * @return gap from -> to in ms.
     */
    public float calculateGapNaive(Car behind, Car infront) {
        RealtimeInfo prev = behind.realtimeRaw;
        RealtimeInfo now = infront.realtimeRaw;
        float splineDistance = (now.getSplinePosition()) - (prev.getSplinePosition());
        float trackDistance = trackData.getTrackMeters() * splineDistance;
        float averageSpeed = (prev.getKMH() + now.getKMH()) / 2f / 3.6f;
        return trackDistance / averageSpeed * 1000;
    }

    private float calculateLapTime() {
        if (trackData.getGt3VelocityMap().isEmpty()) {
            return 0;
        }

        List<Float> velocityMap = trackData.getGt3VelocityMap();

        float stepSize = 1f / velocityMap.size();
        float totalTime = 0;
        float currentPosition = 0;
        //if there is a checkpoint inbetween the current point and the end
        //calculate the time to the checkpoint first.
        while (currentPosition + stepSize < 1) {
            totalTime += calcTimeBetweenSplinePoints(currentPosition, currentPosition + stepSize);
            currentPosition += stepSize;
        }
        //add time to the end point.
        totalTime += calcTimeBetweenSplinePoints(currentPosition, 1);
        return totalTime * 1000;
    }

}
