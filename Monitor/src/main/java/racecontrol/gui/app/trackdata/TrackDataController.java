/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger LOG = Logger.getLogger(TrackDataController.class.getName());

    private final TrackDataPanel panel;

    private final TrackDataExtension trackDataExtension;

    private TrackData trackData;

    private final int mapSize = 200;

    private final List<Float> vMap = new ArrayList<>();
    private final List<List<Float>> vMapTotal = new ArrayList<>();

    private final List<Float> dirMap = new ArrayList<>();
    private final List<List<Float>> dirMapTotal = new ArrayList<>();

    public TrackDataController() {
        EventBus.register(this);
        panel = new TrackDataPanel();
        trackDataExtension = TrackDataExtension.getInstance();

        for (int i = 0; i < mapSize; i++) {
            vMap.add(0f);
            vMapTotal.add(new ArrayList<>());
            dirMap.add(0f);
            dirMapTotal.add(new ArrayList<>());
        }
        panel.vMap = vMap;
        panel.dirMap = dirMap;

        panel.saveButton.setAction(() -> saveAll());
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
            updateDirMap(((RealtimeCarUpdateEvent) e).getInfo());
            panel.drawCarState(((RealtimeCarUpdateEvent) e).getInfo());
            panel.invalidate();
        }
    }

    private void onTrackInfo() {
        trackData = trackDataExtension.getTrackData();

        panel.trackNameLabel.setText(trackData.getTrackname());
        panel.sectorOneTextField.setValue(String.format("%.3f", trackData.getSectorOneLine()).replace(",", "."));
        panel.sectorTwoTextField.setValue(String.format("%.3f", trackData.getSectorTwoLine()).replace(",", "."));
        panel.sectorThreeTextField.setValue(String.format("%.3f", trackData.getSectorThreeLine()).replace(",", "."));
        panel.speedTrapTextField.setValue(String.format("%.3f", trackData.getSpeedTrapLine()).replace(",", "."));

        panel.savedVMap = trackData.getGt3VelocityMap();
        panel.savedDirMap = trackData.getDirectionMap();
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
        }
    }

    private void updateDirMap(RealtimeInfo info) {
        if (info.getCurrentLap().getType() == REGULAR
                && info.getLocation() == TRACK
                && info.getKMH() > 10) {
            int index = (int) Math.floor(info.getSplinePosition() * mapSize);
            if (index == mapSize) {
                index = 0;
            }
            dirMapTotal.get(index).add(info.getYaw());
            dirMapTotal.get(index).sort((a, b) -> a.compareTo(b));
            dirMap.set(index, getMedian(dirMapTotal.get(index)));
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

    private void saveAll() {
        float s1, s2, s3, speedTrap;
        try {
            s1 = Float.parseFloat(panel.sectorOneTextField.getValue());
            s2 = Float.parseFloat(panel.sectorTwoTextField.getValue());
            s3 = Float.parseFloat(panel.sectorThreeTextField.getValue());
            speedTrap = Float.parseFloat(panel.speedTrapTextField.getValue());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error parsing sectors", e);
            return;
        }

        TrackData oldData = trackDataExtension.getTrackData();

        List<Float> newVMap = oldData.getGt3VelocityMap();
        if (panel.enableVMapCheckBox.isSelected()) {
            newVMap = vMap;
        }

        List<Float> newDMap = oldData.getDirectionMap();
        if (panel.enableDMapCheckBox.isSelected()) {
            newDMap = dirMap;
        }

        TrackData newData = new TrackData(oldData.getTrackname(),
                oldData.getTrackMeters(),
                newVMap,
                s1,
                s2,
                s3,
                speedTrap,
                newDMap);
        trackDataExtension.saveTrackData(newData);
    }

}
