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
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.ClientExtension;
import racecontrol.client.data.CarInfo;

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
        updateRatings();

        if (entries.size() > 0 && enabled) {
            CarInfo focus = entries.get(0).getCarInfo();
            if (info.getFocusedCarIndex() != focus.getCarId()) {
                client.sendSetCameraRequestWithFocus(focus.getCarId(), "setVR", "-");
                LOG.info("Chaning focus to " + focus.getCarNumberString() + " with rating: " + entries.get(0).getRating());
            }
        }
    }

    private void updateRatings() {
        entries = getWritableModel().cars.values().stream()
                .map(car -> {
                    Entry entry = new Entry(car.raw);
                    for (RatingProcessor p : processors) {
                        entry = p.calculateRating(entry);
                    }
                    return entry;
                })
                .sorted((c1, c2) -> Float.compare(c2.getRating(), c1.getRating()))
                .collect(Collectors.toList());
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }

}
