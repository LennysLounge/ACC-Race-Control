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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import racecontrol.client.protocol.TrackInfo;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.ClientExtension;

/**
 *
 * @author Leonard
 */
public class TrackDataExtension extends ClientExtension
        implements EventListener {

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
            LOG.info("publishing track data event");
            EventBus.publish(new TrackDataEvent(trackData));
        }
    }

    @SuppressWarnings("unchecked")
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

    public void useTrackData(TrackData trackData) {
        this.trackData = trackData;
        EventBus.publish(new TrackDataEvent(trackData));
    }

    public void saveTrackData(TrackData trackData) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Track Data");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Track data (.trackData)", ".trackData");
        fileChooser.setFileFilter(jsonFilter);

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String name = fileChooser.getSelectedFile().getParent() + "\\" + trackData.getTrackname() + ".trackData";
            try {
                FileOutputStream fos = new FileOutputStream(name);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(trackData);
                oos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TrackDataExtension.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TrackDataExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
