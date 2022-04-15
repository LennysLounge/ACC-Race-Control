/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.ClientExtension;

/**
 *
 * @author Leonard
 */
public class AutobroadcastExtension extends ClientExtension
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static AutobroadcastExtension instance;
    /**
     * This calss's logger.
     */
    private static final Logger LOG = Logger.getLogger(AutobroadcastExtension.class.getName());
    /**
     * List of ratings processors.
     */
    private final List<RatingProcessor> processors = new ArrayList<>();
    /**
     * List of current car ratings.
     */
    private List<CarRating> carRatings = new ArrayList<>();
    /**
     * List of camera ratings.
     */
    private final List<CameraRating> cameraRatings = new ArrayList<>();
    /**
     * Whether or not the extension is enabled.
     */
    private boolean enabled = false;
    /**
     * Last camera update timestmap.
     */
    private long lastCameraUpdate = 0;
    /**
     * Last cam change timestmap.
     */
    private long lastCameraChange = 0;
    /**
     * Current camera rating.
     */
    private CameraRating currentCameraRating = null;

    private int currentFocusedCarId = 0;

    private String currentCameraSet = "";

    public static AutobroadcastExtension getInstance() {
        if (instance == null) {
            instance = new AutobroadcastExtension();
        }
        return instance;
    }

    private AutobroadcastExtension() {
        EventBus.register(this);
        processors.add(new RatingProcessorImpl());

        cameraRatings.add(new CameraRating("set1", 0.35f));
        cameraRatings.add(new CameraRating("set2", 0.35f));
        cameraRatings.add(new CameraRating("Helicam", 0.15f));
        cameraRatings.add(new CameraRating("Onboard", 0.15f));
        currentCameraRating = cameraRatings.get(0);
    }

    @Override
    public void onEvent(Event e) {
        processors.forEach(p -> p.onEvent(e));
        if (e instanceof RealtimeUpdateEvent) {
            testCameraChange(((RealtimeUpdateEvent) e).getSessionInfo());
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    private void testCameraChange(SessionInfo info) {
        if (info.getFocusedCarIndex() != currentFocusedCarId
                || !info.getActiveCameraSet().equals(currentCameraSet)) {
            currentFocusedCarId = info.getFocusedCarIndex();
            currentCameraSet = info.getActiveCameraSet();
            lastCameraChange = System.currentTimeMillis();
            lastCameraUpdate = lastCameraChange;
            currentCameraRating = null;
            for (var cameraRating : cameraRatings) {
                if (cameraRating.camSet.equals(currentCameraSet)) {
                    currentCameraRating = cameraRating;
                    break;
                }
            }
            LOG.info("cam changed to " + info.getActiveCameraSet() + " " + currentCameraRating);
        }
    }

    private void onSessionUpdate(SessionInfo info) {
        carRatings = updateCarRatings();
        updateCameraRatings();

        // If the autopilot is not enabled we dont need to do anything else. 
        if (!enabled) {
            return;
        }
        if (carRatings.isEmpty()) {
            return;
        }

        // sort camera's by rating and switch to the best rated camera
        cameraRatings.sort((c1, c2) -> -Float.compare(c1.getRating(), c2.getRating()));
        if (currentCameraRating != cameraRatings.get(0)) {
            LOG.info("Changing camera to " + cameraRatings.get(0).camSet);
            getClient().sendSetCameraRequest(cameraRatings.get(0).camSet, "Onboard0");
        }
    }

    private List<CarRating> updateCarRatings() {
        return getWritableModel().cars.values().stream()
                .filter(car -> car.connected)
                .map(car -> {
                    CarRating entry = new CarRating(car);
                    for (RatingProcessor p : processors) {
                        entry = p.calculateRating(entry);
                    }
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private void updateCameraRatings() {
        // update the screen time for the current camera
        long now = System.currentTimeMillis();
        if (currentCameraRating != null) {
            currentCameraRating.screenTime += now - lastCameraUpdate;
        }
        lastCameraUpdate = now;

        int totalScreenTime = cameraRatings.stream()
                .map(rating -> rating.screenTime)
                .reduce(0, Integer::sum);

        for (var camera : cameraRatings) {
            // Over representation in screen time will reduce the rating.
            // Under representation will increase it.
            float screenTimeError = camera.screenTime - camera.screenTimeShare * totalScreenTime;
            camera.screenTimeError = 1 - screenTimeError / 60000f;

            // focus penalty
            if (camera == currentCameraRating) {
                camera.focus = 1f;
            } else {
                camera.focus = clamp((now - lastCameraChange) / 30000f);
            }
        }
    }

    public List<CarRating> getCarEntries() {
        return carRatings;
    }

    public List<CameraRating> getCameraRatings() {
        return cameraRatings;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
        if (this.enabled) {
            // reset camera settings.
            for (var camera : cameraRatings) {
                camera.screenTime = 0;
            }
            lastCameraChange = System.currentTimeMillis();
            lastCameraUpdate = lastCameraChange;
        }
    }

    private float clamp(float v) {
        return Math.max(0, Math.min(1, v));
    }

}
