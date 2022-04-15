/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

/**
 *
 * @author Leonard
 */
public class CameraRating {

    /**
     * The camera set this rating represents.
     */
    public final String camSet;
    /**
     * Expected percentage of the total screen time.
     */
    public final float screenTimeShare;
    /**
     * Describes how good the camera is in the current situation.
     */
    public float situation = 0;
    /**
     * Screen time in ms.
     */
    public int screenTime = 0;
    /**
     * The error between the expected screen time for this camera and the actual
     * screen time.
     */
    public float screenTimeError = 0;
    /**
     * Focus penalty to prevent rapid switching.
     */
    public float focus;

    public CameraRating(String camSet, float screenTimeShare) {
        this.camSet = camSet;
        this.screenTimeShare = screenTimeShare;
    }

    public float getRatingNoFocus() {
        return situation + screenTimeError;
    }

    public float getRating() {
        return getRatingNoFocus() * focus;
    }

}
