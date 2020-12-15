/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.networking.data;

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
        return cameraSets;
    }

    public List<String> getHudPages() {
        return hudPages;
    }

}
