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
import static java.util.stream.Collectors.toList;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class AutobroadcastExtension
        implements EventListener, AccBroadcastingExtension {

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

    public static AutobroadcastExtension getInstance() {
        if (instance == null) {
            instance = new AutobroadcastExtension();
        }
        return instance;
    }

    private AutobroadcastExtension() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        processors.add(new ProximityProcessor());
        processors.add(new NoQuickChangeProcessor());
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
        entries = entries.stream()
                .sorted((c1, c2) -> Float.compare(c2.getRating(), c1.getRating()))
                .collect(toList());

        if (entries.size() > 0) {
            int focus = entries.get(0).getCarInfo().getCarId();
            if (info.getFocusedCarIndex() != focus) {
                client.sendSetCameraRequestWithFocus(focus, "setVR", "-");
            }
        }
    }

    private void updateRatings() {
        entries = client.getModel().getCarsInfo().values().stream()
                .map(carInfo -> {
                    Entry entry = new Entry(carInfo);
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

}
