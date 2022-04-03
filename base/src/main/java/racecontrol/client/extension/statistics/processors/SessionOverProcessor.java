/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.Map;
import racecontrol.client.AccBroadcastingClient;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.protocol.SessionInfo;
import static racecontrol.client.protocol.enums.SessionPhase.SESSIONOVER;
import static racecontrol.client.protocol.enums.SessionType.PRACTICE;
import static racecontrol.client.protocol.enums.SessionType.QUALIFYING;
import static racecontrol.client.protocol.enums.SessionType.RACE;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.client.extension.laptimes.LapCompletedEvent;
import static racecontrol.client.extension.statistics.CarStatistics.SESSION_FINISHED;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.CarStatisticsWritable;
import racecontrol.client.model.Car;
import racecontrol.eventbus.Event;

/**
 * Finds when a car has finished its session.
 *
 * @author Leonard
 */
public class SessionOverProcessor
        extends StatisticsProcessor {

    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;

    private boolean isSessionOver = false;

    public SessionOverProcessor(Map<Integer, CarStatisticsWritable> cars) {
        super(cars);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof LapCompletedEvent) {
            onLapCompleted(((LapCompletedEvent) e).getCar());
        } else if (e instanceof SessionChangedEvent) {
            onSessionChanged(((SessionChangedEvent) e).getSessionInfo());
        } else if (e instanceof SessionPhaseChangedEvent) {
            onSessionPhaseChanged(((SessionPhaseChangedEvent) e).getSessionInfo());
        }
    }

    private void onLapCompleted(Car car) {
        // The session is over when the leading car finishes his lap and
        // the session phase is "SESSIONOVER" during a race.
        SessionInfo info = getClient().getModel().session.raw;
        if (info.getSessionType() == RACE
                && info.getPhase() == SESSIONOVER
                && car.realtimeRaw.getPosition() == 1) {
            isSessionOver = true;
        }

        // when the session is over we set the finished flag for the car.
        if (isSessionOver) {
            getCars().get(car.id).put(SESSION_FINISHED, true);
        }
    }

    private void onSessionChanged(SessionInfo info) {
        // reset finished flag
        getCars().values().forEach(carStats -> carStats.put(SESSION_FINISHED, false));
        isSessionOver = false;
    }

    private void onSessionPhaseChanged(SessionInfo info) {
        // the session is over when the pase changes to "SESSIONOVER" during
        // practice and qualifying.
        if (info.getPhase() == SESSIONOVER
                && (info.getSessionType() == PRACTICE
                || info.getSessionType() == QUALIFYING)) {
            isSessionOver = true;
        }
    }

}
