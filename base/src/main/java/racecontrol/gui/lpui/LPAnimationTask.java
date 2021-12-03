/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui;

/**
 *
 * @author Leonard
 */
public class LPAnimationTask {

    /**
     * Shows that the task is currently running.
     */
    private boolean running;
    /**
     * The actual funtion that does the animation.
     */
    private final LPAnimationFunction animationFunction;
    /**
     * Duration of the animation in milliseconds.
     */
    private int duration;
    /**
     * Amount of time this animation has been animating in milliseconds.
     */
    private int progress;

    public LPAnimationTask(LPAnimationFunction animationFunction) {
        this.animationFunction = animationFunction;
    }
    
    public LPAnimationTask(LPAnimationFunction animationFunction, int duration) {
        this.animationFunction = animationFunction;
        this.duration = duration;
    }

    /**
     * Set the duration of the animation. Duration of -1 has an infinite
     * duration.
     *
     * @param duration the duration of the animation.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Starts the animation.
     */
    public void start() {
        running = true;
    }

    /**
     * Stars the animation from the begining.
     */
    public void restart() {
        running = true;
        progress = 0;
    }

    /**
     * Stops the animation.
     */
    public void stop() {
        running = false;
    }

    /**
     * Runs a single animation step.
     *
     * @param dt The delta time for this animation step.
     */
    void animate(int dt) {
        if (isFinished()) {
            return;
        }
        if (!running) {
            return;
        }

        if (progress + dt > duration) {
            dt = duration - progress;
        }
        progress += dt;
        animationFunction.animate(this, dt);

        if (isFinished()) {
            running = false;
        }
    }

    /**
     * Returns the duration.
     *
     * @return the duration.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the progress.
     *
     * @return the progress.
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Returns the progress from 0 to 1.
     *
     * @return the progress from 0 to 1.
     */
    public float getProgressNormal() {
        return progress * 1f / duration * 1f;
    }

    /**
     * Returns whether or not the animation is finished.
     *
     * @return whether or not the animation is finished.
     */
    public boolean isFinished() {
        if (duration < 0) {
            return false;
        }
        return progress >= duration;
    }

    @FunctionalInterface
    public interface LPAnimationFunction {

        void animate(LPAnimationTask task, int deltaTime);
    }
}
