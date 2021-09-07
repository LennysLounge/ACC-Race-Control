/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.velocitymap;

import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.TrackInfo;
import static racecontrol.client.data.enums.CarLocation.TRACK;
import static racecontrol.client.data.enums.LapType.REGULAR;
import racecontrol.client.events.RealtimeCarUpdate;
import racecontrol.client.events.TrackData;
import racecontrol.lpgui.gui.LPContainer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;

/**
 *
 * @author Leonard
 */
public class VelocityMapExtension
        extends AccClientExtension {

    private final static Logger LOG = Logger.getLogger(VelocityMapExtension.class.getName());

    private final VelocityMapPanel panel;
    private final List<Float> velocityMap = new ArrayList<>();
    private final List<List<Float>> velocityMapTotal = new ArrayList<>();
    private final int mapSize = 200;
    private String trackName = "none";

    public VelocityMapExtension(AccBroadcastingClient client) {
        super(client);
        panel = new VelocityMapPanel(this);
        for (int i = 0; i < mapSize; i++) {
            velocityMap.add(-1f);
            velocityMapTotal.add(new ArrayList<>());
        }
        panel.setVelocityMap(velocityMap);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof TrackData) {
            TrackInfo info = ((TrackData) e).getInfo();
            panel.setTrackName(info.getTrackName());
            trackName = info.getTrackName();
        } else if (e instanceof RealtimeCarUpdate) {
            RealtimeInfo info = ((RealtimeCarUpdate) e).getInfo();
            if (info.getCurrentLap().getType() == REGULAR
                    && info.getLocation() == TRACK
                    && info.getKMH() > 10
                    && !info.getCurrentLap().isInvalid()
                    && info.getLaps() > 0) {
                int index = (int) Math.floor(info.getSplinePosition() * mapSize);
                if (index == mapSize) {
                    index = 0;
                }
                velocityMapTotal.get(index).add(info.getKMH() * 1f);
                velocityMapTotal.get(index).sort((a, b) -> a.compareTo(b));
                velocityMap.set(index, getMedian(velocityMapTotal.get(index)));
                /*
                if (velocityMap.get(index) < 0) {
                    
                    if (info.getKMH() * 1f > velocityMap.get(index) * 0.9f) {
                        velocityMap.set(index, info.getKMH() * 1f);
                    }
                    
                } else {
                    float v = velocityMap.get(index);
                    velocityMap.set(index, v * 0.99f + info.getKMH() * 0.01f);
                }
                 */
                panel.onRealtimeCarUpdate(info.getSplinePosition(), info.getKMH());
                panel.setVelocityMap(velocityMap);
                panel.invalidate();
            }
        }
    }

    public void saveVelocityMap() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(trackName + ".vMap");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(velocityMap);
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
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
