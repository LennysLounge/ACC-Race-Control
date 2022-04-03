/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Leonard
 */
public class TrackInfo {

    private String trackName = "";
    private int trackId;
    private int trackMeters;
    private Map<String, List<String>> cameraSets = new HashMap<>();
    private List<String> hudPages = new LinkedList<>();

    public TrackInfo() {
    }

    public TrackInfo(String trackName, int trackId, int trackMeters, Map<String, List<String>> cameraSets, List<String> hudPages) {
        this.trackName = trackName;
        this.trackId = trackId;
        this.trackMeters = trackMeters;
        this.cameraSets = cameraSets;
        this.hudPages = hudPages;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getTrackId() {
        return trackId;
    }

    public int getTrackMeters() {
        return trackMeters;
    }

    public Map<String, List<String>> getCameraSets() {
        return Collections.unmodifiableMap(cameraSets);
    }

    public List<String> getHudPages() {
        return Collections.unmodifiableList(hudPages);
    }

}
