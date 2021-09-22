/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.List;
import racecontrol.client.data.RealtimeInfo;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.data.enums.LapType.REGULAR;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.client.extension.trackdata.TrackData;
import racecontrol.client.extension.trackdata.TrackDataExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class TrackDataController
        implements EventListener {

    private final TrackDataPanel panel;

    private final TrackDataExtension trackDataExtension;

    private TrackData trackData;

    private final int mapSize = 200;

    private final List<Float> vMap = new ArrayList<>();
    private final List<List<Float>> vMapTotal = new ArrayList<>();

    public TrackDataController() {
        EventBus.register(this);
        panel = new TrackDataPanel();
        trackDataExtension = TrackDataExtension.getInstance();

        for (int i = 0; i < mapSize; i++) {
            vMap.add(-1f);
            vMapTotal.add(new ArrayList<>());
        }
        panel.vMap = vMap;
    }

    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackInfoEvent) {
            onTrackInfo();
        } else if (e instanceof RealtimeCarUpdateEvent) {
            updateVMap(((RealtimeCarUpdateEvent) e).getInfo());
        }
    }

    private void onTrackInfo() {
        trackData = trackDataExtension.getTrackData();

        panel.trackNameLabel.setText(trackData.getTrackname());
        panel.sectorOneTextField.setValue(String.format("%.3f", trackData.getSectorOneLine()));
        panel.sectorTwoTextField.setValue(String.format("%.3f", trackData.getSectorTwoLine()));
        panel.sectorThreeTextField.setValue(String.format("%.3f", trackData.getSectorThreeLine()));

        panel.savedVMap = trackData.getGt3VelocityMap();
    }

    private void updateVMap(RealtimeInfo info) {
        if (info.getCurrentLap().getType() == REGULAR
                && info.getLocation() == TRACK
                && info.getKMH() > 10
                && !info.getCurrentLap().isInvalid()
                && info.getLaps() > 0) {
            int index = (int) Math.floor(info.getSplinePosition() * mapSize);
            if (index == mapSize) {
                index = 0;
            }
            vMapTotal.get(index).add(info.getKMH() * 1f);
            vMapTotal.get(index).sort((a, b) -> a.compareTo(b));
            vMap.set(index, getMedian(vMapTotal.get(index)));
            
            panel.addVMapPoint(info.getSplinePosition(), info.getKMH());
        }
    }

    private float getMedian(List<Float> l) {
        if (l.size() < 1) {
            return 0;
        }
        int middle = (int) Math.floor(l.size() / 2f);
        if (l.size() % 2 == 0) {
            return (l.get(middle) + l.get(middle - 1)) / 2f;
        } else {
            return l.get(middle);
        }
    }

}
