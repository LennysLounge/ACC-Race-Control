/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.client.ClientExtension;
import racecontrol.client.events.CarConnectedEvent;
import static racecontrol.client.protocol.enums.SessionType.RACE;

/**
 *
 * @author Leonard
 */
public class AutobroadcastExtension
        extends ClientExtension {

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

    private long lastCarUpdate = 0;

    public static AutobroadcastExtension getInstance() {
        if (instance == null) {
            instance = new AutobroadcastExtension();
        }
        return instance;
    }

    private AutobroadcastExtension() {
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
        } else if (e instanceof CarConnectedEvent) {
            carRatings.add(new CarRating(((CarConnectedEvent) e).getCar()));
        }
    }

    private void testCameraChange(SessionInfo info) {
        if (info.getFocusedCarIndex() != currentFocusedCarId
                || !info.getActiveCameraSet().equals(currentCameraSet)) {
            currentFocusedCarId = info.getFocusedCarIndex();
            currentCameraSet = info.getActiveCameraSet();
            lastCameraChange = System.currentTimeMillis();
            lastCameraUpdate = lastCameraChange;
            lastCarUpdate = lastCameraChange;
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
        updateCarRatings();
        updateCameraRatings();

        // If the autopilot is not enabled we dont need to do anything else. 
        if (!enabled) {
            return;
        }
        if (carRatings.isEmpty()) {
            return;
        }

        // find the car we should focus on.
        if (info.getSessionType() == RACE && info.getSessionTime() < 15000) {
            // always focus on leader at the start of the race.
            carRatings.sort((c1, c2) -> Integer.compare(c1.car.position, c2.car.position));
        } else {
            carRatings.sort((c1, c2) -> -Float.compare(c1.getRating(), c2.getRating()));
        }
        CarRating nextCar = carRatings.get(0);

        // sort camera's by rating and switch to the best rated camera
        cameraRatings.sort((c1, c2) -> -Float.compare(c1.getRating(), c2.getRating()));
        CameraRating nextCamera = cameraRatings.get(0);

        if (currentFocusedCarId != nextCar.car.id
                || currentCameraRating != nextCamera) {
            LOG.info("Changing camera to " + nextCar.car.carNumberString() + " and " + cameraRatings.get(0).camSet);
            getClient().sendSetCameraRequestWithFocus(nextCar.car.id,
                    nextCamera.camSet,
                    "Onboard0");
        }

    }

    private void updateCarRatings() {
        // remove cars that are not connected.
        var iterator = carRatings.iterator();
        while (iterator.hasNext()) {
            CarRating car = iterator.next();
            if (!car.car.connected) {
                iterator.remove();
            }
            // else update car
        }

        // update screen time for current car.
        long now = System.currentTimeMillis();
        for (CarRating rating : carRatings) {
            if (rating.car.isFocused) {
                rating.screenTime += now - lastCarUpdate;
            }
        }
        lastCarUpdate = now;

        // update rating from all cars.
        for (RatingProcessor p : processors) {
            p.calculateRating(carRatings);
        }
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
            lastCarUpdate = lastCameraChange;
        }
    }

    private float clamp(float v) {
        return Math.max(0, Math.min(1, v));
    }

}
