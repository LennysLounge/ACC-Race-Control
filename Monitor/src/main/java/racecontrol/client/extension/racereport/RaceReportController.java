/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.racereport;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import racecontrol.client.data.SessionId;
import static racecontrol.client.data.enums.SessionType.RACE;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.extensions.laptimes.LapCompletedEvent;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class RaceReportController
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static RaceReportController instance;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(RaceReportController.class.getName());
    /**
     * maps sessionId to a session. Session maps a carId to a driver Record.
     */
    private final Map<SessionId, Map<Integer, DriverRecord>> sessions = new HashMap<>();
    /**
     * Leader offset maps a lap number to a timestamp when the leader completed
     * that lap.
     */
    private final Map<Integer, Long> leaderOffset = new HashMap<>();
    /**
     * current session id.
     */
    private SessionId sessionId;

    public static RaceReportController getInstance() {
        if (instance == null) {
            instance = new RaceReportController();
        }
        return instance;
    }

    private RaceReportController() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof LapCompletedEvent) {
            onLapCompleted((LapCompletedEvent) e);
        } else if (e instanceof SessionChangedEvent) {
            sessionId = ((SessionChangedEvent) e).getSessionId();
            sessions.put(sessionId, new HashMap<>());
            leaderOffset.clear();
        }
    }

    private void onLapCompleted(LapCompletedEvent e) {
        if (!sessions.get(sessionId).containsKey(e.getCar().getCarId())) {
            String driverName = e.getCar().getDrivers().stream()
                    .map(driverInfo -> driverInfo.getFirstName() + " " + driverInfo.getLastName())
                    .collect(Collectors.joining(", "));
            sessions.get(sessionId).put(e.getCar().getCarId(),
                    new DriverRecord(
                            driverName,
                            String.valueOf(e.getCar().getCarNumber())
                    )
            );
        }
        DriverRecord dr = sessions.get(sessionId).get(e.getCar().getCarId());

        //set position and lap count
        dr.setPosition(e.getCar().getRealtime().getPosition());
        dr.setLapCount(e.getCar().getRealtime().getLaps());

        float deltaToLeader = 0;

        // if we are in a race session, calculate offset to leader.
        if (sessionId.getType() == RACE) {
            //if this car is the leader add the leader offset.
            int lapCount = e.getCar().getRealtime().getLaps();
            long now = System.currentTimeMillis();
            if (!leaderOffset.containsKey(lapCount)) {
                leaderOffset.put(lapCount, now);
            }

            deltaToLeader = now - leaderOffset.get(lapCount);
        }

        dr.getLaps().add(new LapRecord(e.getLapTime(), deltaToLeader));
        LOG.info("Lap recorded for #" + e.getCar().getCarNumber()
                + "\ttime: " + TimeUtils.asLapTime(e.getLapTime())
                + "\toffset: " + TimeUtils.asDelta(deltaToLeader));
    }
    
    /**
     * Saves a race report to disk.
     */
    public void saveRaceReport(){
        
    }

}
