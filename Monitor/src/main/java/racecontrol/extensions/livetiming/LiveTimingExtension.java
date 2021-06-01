/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.livetiming;

import racecontrol.extensions.livetiming.tablemodels.RaceTableModel;
import racecontrol.extensions.livetiming.tablemodels.LiveTimingTableModel;
import racecontrol.extensions.livetiming.tablemodels.QualifyingTableModel;
import racecontrol.client.events.RealtimeCarUpdate;
import racecontrol.client.events.RealtimeUpdate;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.SessionChanged;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.TrackInfo;
import racecontrol.client.data.enums.SessionType;
import racecontrol.client.events.CarDisconnect;
import racecontrol.client.events.TrackData;
import racecontrol.utility.GapCalculator;
import racecontrol.visualisation.gui.LPContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtension
        extends AccClientExtension {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(LiveTimingExtension.class.getName());
    /**
     * The visualisation panel
     */
    private final LiveTimingPanel panel;
    /**
     * Map from carId to ListEntry.
     */
    private final Map<Integer, LiveTimingEntry> entries = new HashMap<>();
    /**
     * Table model to display the live timing.
     */
    private LiveTimingTableModel model = new QualifyingTableModel();
    /**
     * current session type.
     */
    private SessionType currentSession = SessionType.PRACTICE;
    /**
     * Current track info.
     */
    private TrackInfo trackInfo;
    /**
     * Calculator to calculate the live gaps between cars.
     */
    private final GapCalculator gapCalculator = new GapCalculator();

    public LiveTimingExtension(AccBroadcastingClient client) {
        super(client);
        this.panel = new LiveTimingPanel(this);
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    public LiveTimingTableModel getTableModel() {
        return model;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdate) {
            onRealtimeUpdate(((RealtimeUpdate) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdate) {
            onRealtimeCarUpdate((RealtimeCarUpdate) e);
        } else if (e instanceof CarDisconnect) {
            CarDisconnect dis = ((CarDisconnect) e);
            if (entries.containsKey(dis.getCar().getCarId())) {
                entries.remove(dis.getCar().getCarId());
            }
        } else if (e instanceof SessionChanged) {
            SessionType newSession = ((SessionChanged) e).getSessionInfo().getSessionType();
            if (newSession != currentSession) {
                currentSession = newSession;
                if (newSession == SessionType.RACE) {
                    model = new RaceTableModel();
                } else {
                    model = new QualifyingTableModel();
                }
                panel.setTableModel(model);
            }
        } else if (e instanceof TrackData) {
            trackInfo = ((TrackData) e).getInfo();
            gapCalculator.loadVMapForTrack(trackInfo.getTrackName());
            gapCalculator.setTrackLength(trackInfo.getTrackMeters());
        }
    }

    public void onRealtimeUpdate(SessionInfo sessionInfo) {
        List<LiveTimingEntry> sortedEntries = new LinkedList<>(entries.values());
        sortedEntries = sortedEntries.stream()
                .sorted((e1, e2) -> comparePosition(e1, e2))
                .collect(Collectors.toList());
        if (currentSession == SessionType.RACE) {
            /*
            sortedEntries = sortedEntries.stream()
                    .sorted((e1, e2) -> compareRaceDistance(e1, e2))
                    .collect(Collectors.toList());
             */
            sortedEntries = calculateGaps(sortedEntries);
        }

        //find best sectors.
        List<Integer> sessionBestSectors = new ArrayList<>(Arrays.asList(9999999, 9999999, 9999999));
        for (LiveTimingEntry entry : sortedEntries) {
            LapInfo bestLap = entry.getCarInfo().getRealtime().getBestSessionLap();
            for (int i = 0; i < bestLap.getSplits().size(); i++) {
                if (bestLap.getSplits().get(i) < sessionBestSectors.get(i)) {
                    sessionBestSectors.set(i, bestLap.getSplits().get(i));
                }
            }
        }

        model.setEntries(sortedEntries);
        model.setFocusedCarId(sessionInfo.getFocusedCarIndex());
        model.setSessionBestLap(sessionInfo.getBestSessionLap());
        model.setSessionBestSectors(sessionBestSectors);
        panel.invalidate();
    }

    private List<LiveTimingEntry> calculateGaps(List<LiveTimingEntry> list) {
        if (list.isEmpty()) {
            return list;
        }

        List<LiveTimingEntry> withGaps = new LinkedList<>();
        withGaps.add(list.get(0));
        RealtimeInfo leaderRealtimeInfo = list.get(0).getCarInfo().getRealtime();
        float leaderRaceDistance = leaderRealtimeInfo.getLaps() + leaderRealtimeInfo.getSplinePosition();
        int splitLapsBehind = 0;
        for (int i = 1; i < list.size(); i++) {

            //calculate gap to car infront and increment total time
            float gap = gapCalculator.calculateGap(list.get(i).getCarInfo(), list.get(i - 1).getCarInfo());
            float gapBehindLeader = gapCalculator.calculateGap(list.get(i).getCarInfo(), list.get(0).getCarInfo());

            RealtimeInfo carRealtimeInfo = list.get(i).getCarInfo().getRealtime();
            float carRaceDistance = carRealtimeInfo.getLaps() + carRealtimeInfo.getSplinePosition();
            int lapsBehind = (int) Math.floor(leaderRaceDistance - carRaceDistance);
            boolean showLapDiff = false;
            if (lapsBehind > splitLapsBehind) {
                showLapDiff = true;
                splitLapsBehind = lapsBehind;
            }

            withGaps.add(new LiveTimingEntry(
                    list.get(i).getCarInfo(),
                    gap,
                    gapBehindLeader,
                    showLapDiff,
                    lapsBehind
            ));
        }
        return withGaps;
    }

    public void onRealtimeCarUpdate(RealtimeCarUpdate event) {
        RealtimeInfo info = event.getInfo();
        CarInfo car = getClient().getModel().getCarsInfo().get(info.getCarId());
        if (car != null) {
            entries.put(car.getCarId(), new LiveTimingEntry(car));
        }
    }

    private int comparePosition(LiveTimingEntry e1, LiveTimingEntry e2) {
        RealtimeInfo r1 = e1.getCarInfo().getRealtime();
        RealtimeInfo r2 = e2.getCarInfo().getRealtime();
        return (int) Math.signum(r1.getPosition() - r2.getPosition());
    }

    private int compareRaceDistance(LiveTimingEntry e1, LiveTimingEntry e2) {
        RealtimeInfo r1 = e1.getCarInfo().getRealtime();
        RealtimeInfo r2 = e2.getCarInfo().getRealtime();
        return (int) Math.signum((r2.getLaps() + r2.getSplinePosition()) - (r1.getLaps() + r1.getSplinePosition()));
    }

    public void focusOnCar(CarInfo car) {
        getClient().sendChangeFocusRequest(car.getCarId());
    }

    private boolean isFocused(CarInfo car) {
        return car.getCarId() == getClient().getModel().getSessionInfo().getFocusedCarIndex();
    }

}
