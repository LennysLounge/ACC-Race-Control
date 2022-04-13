/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.ClientExtension;
import racecontrol.client.events.ConnectionOpenedEvent;
import static racecontrol.client.protocol.enums.SessionType.RACE;

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
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;
    /**
     * List of ratings processors.
     */
    private final List<RatingProcessor> processors = new ArrayList<>();
    /**
     * List of current entries.
     */
    private List<Entry> entries = new ArrayList<>();
    /**
     * Whether or not the extension is enabled.
     */
    private boolean enabled = false;
    /**
     * Next cam change timestmap.
     */
    private long nextCamChange = 0;
    /**
     * Last cam change timestmap.
     */
    private long lastSessionUpdate = 0;
    /**
     * Counts the screentime of each camera in milliseconds.
     */
    private final long[] camScreenTime = {0, 0, 0, 0};
    /**
     * Expected screen time for each camera.
     */
    private final float[] expectedScreenTime = {0.35f, 0.35f, 0.15f, 0.15f};
    /**
     * Current camera index.
     */
    private int currentCamera = 0;

    public static AutobroadcastExtension getInstance() {
        if (instance == null) {
            instance = new AutobroadcastExtension();
        }
        return instance;
    }

    private AutobroadcastExtension() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        processors.add(new RatingProcessorImpl());
    }

    @Override
    public void onEvent(Event e) {
        processors.forEach(p -> p.onEvent(e));
        if (e instanceof RealtimeUpdateEvent) {
            onSessionUpdate(((RealtimeUpdateEvent) e).getSessionInfo());
        }
    }

    private void onSessionUpdate(SessionInfo info) {
        entries = updateRatings();

        // If the autopilot is not enabled we dont need to do anything else. 
        if (!enabled) {
            return;
        }
        if (entries.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        // update screen time of the current camera.
        camScreenTime[currentCamera] += now - lastSessionUpdate;
        lastSessionUpdate = now;

        if (now > nextCamChange) {
            // duration of the next camera.
            int camDuration = ThreadLocalRandom.current().nextInt(5, 26) * 1000;

            // find which camera to use next to meet the expected screen time.
            long totalScreenTime = camScreenTime[0]
                    + camScreenTime[1]
                    + camScreenTime[2]
                    + camScreenTime[3]
                    + camDuration;
            int nextCamIndex = 0;
            float bestError = 999999;
            for (int i = 0; i < 4; i++) {
                float error = camScreenTime[i] - expectedScreenTime[i] * totalScreenTime;
                if (error < bestError) {
                    nextCamIndex = i;
                    bestError = error;
                }
            }
            
            // translate to camSet and camera
            String nextCamSet = "set1";
            String nextCamera = "-";
            switch (nextCamIndex) {
                case 0:
                    nextCamSet = "set1";
                    nextCamera = "-";
                    break;
                case 1:
                    nextCamSet = "set2";
                    nextCamera = "-";
                    break;
                case 2:
                    nextCamSet = "Helicam";
                    nextCamera = "-";
                    break;
                case 3:
                    nextCamSet = "Onboard";
                    nextCamera = "Onboard0";
                    break;
            }

            // find car to focus on.
            List<Entry> cars = entries;
            if (info.getSessionType() == RACE && info.getSessionTime() < 15000) {
                // always choose leader for first 15 seconds.
                cars.sort((e1, e2) -> Integer.compare(e1.car.realtimePosition, e2.car.realtimePosition));
            } else {
                cars.sort((e1, e2) -> -Float.compare(e1.getRating(), e2.getRating()));
            }
            Entry nextCar = cars.get(0);

            // switch camera if necessary
            if (info.getFocusedCarIndex() != nextCar.car.id
                    || !info.getActiveCameraSet().equals(nextCamSet)) {
                LOG.info("Autobroadcast setting camera to car "
                        + nextCar.car.carNumberString()
                        + " to camset " + nextCamSet
                        + " and camera " + nextCamera);
                client.sendSetCameraRequestWithFocus(nextCar.car.id,
                        nextCamSet,
                        nextCamera);
            }

            currentCamera = nextCamIndex;
            nextCamChange = now + camDuration;
        }
    }

    private List<Entry> updateRatings() {
        return getWritableModel().cars.values().stream()
                .filter(car -> car.connected)
                .map(car -> {
                    Entry entry = new Entry(car);
                    for (RatingProcessor p : processors) {
                        entry = p.calculateRating(entry);
                    }
                    return entry;
                })
                .collect(Collectors.toList());
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
        if (this.enabled) {
            // reset camera settings.
            camScreenTime[0] = 0;
            camScreenTime[1] = 0;
            camScreenTime[2] = 0;
            camScreenTime[3] = 0;
            currentCamera = 0;
            lastSessionUpdate = System.currentTimeMillis();
            nextCamChange = lastSessionUpdate;
        }
    }

    public long getNextCamChange() {
        return nextCamChange;
    }

    public List<Long> getCamScreenTime() {
        return Arrays.stream(camScreenTime)
                .boxed()
                .collect(Collectors.toList());
    }

}
