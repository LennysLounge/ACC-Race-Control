/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.trackdata;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.client.extension.velocitymap.VelocityMapExtension;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class TrackDataExtension
        implements EventListener, AccBroadcastingExtension {

    private static final Logger LOG = Logger.getLogger(TrackDataExtension.class.getName());

    /**
     * Singelton instance.
     */
    private static TrackDataExtension instance;

    private TrackData trackData;

    public static TrackDataExtension getInstance() {
        if (instance == null) {
            instance = new TrackDataExtension();
        }
        return instance;
    }

    private TrackDataExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackInfoEvent) {
            loadTrackData(((TrackInfoEvent) e).getInfo());
            loadVelocityMapAndSaveToTrackData(((TrackInfoEvent) e).getInfo());
        }
    }

    private void loadTrackData(TrackInfo info) {
        try {
            InputStream in = getClass().getResourceAsStream("/trackdata/" + info.getTrackName() + ".trackData");
            ObjectInputStream objIn = new ObjectInputStream(in);
            trackData = (TrackData) objIn.readObject();
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            LOG.log(Level.WARNING, info.getTrackName() + " track data not found or could not be read.", ex);
            trackData = new TrackData(info.getTrackName(), info.getTrackMeters());
        }
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public void saveTrackData(TrackData trackData) {
        try {
            FileOutputStream fos = new FileOutputStream(trackData.getTrackname() + ".trackData");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trackData);
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadVelocityMapAndSaveToTrackData(TrackInfo info) {
        List<Float> vmap = loadVMapForTrack(info.getTrackName());
        TrackData data = new TrackData(info.getTrackName(), info.getTrackMeters(), vmap, 0.333f, 0.666f, 1f, null);
        saveTrackData(data);
    }

    public List<Float> loadVMapForTrack(String trackName) {
        try {
            InputStream in = getClass().getResourceAsStream("/velocitymap/" + trackName + ".vMap");
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (List<Float>) objIn.readObject();
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            LOG.log(Level.WARNING, trackName + " velocity map not found", ex);
        }
        return null;
    }

}
