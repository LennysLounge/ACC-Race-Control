/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class AccBroadcastingData {

    private static Logger LOG = Logger.getLogger(AccBroadcastingData.class.getName());

    private Map<Integer, CarInfo> cars = new HashMap<>();

    public AccBroadcastingData() {
    }

    public AccBroadcastingData(Map<Integer, CarInfo> cars, SessionInfo session,
            TrackInfo trackInfo, List<BroadcastingEvent> events) {
        this.cars = requireNonNull(cars, "cars");
    }
}
