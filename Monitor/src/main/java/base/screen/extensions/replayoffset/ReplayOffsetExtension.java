/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.replayoffset;

import base.screen.Main;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.networking.BroadcastingEventEvent;
import base.screen.networking.data.BroadcastingEvent;
import base.screen.networking.events.SessionChanged;
import base.screen.visualisation.gui.LPContainer;
import java.util.logging.Logger;

/**
 * Calculates the replay start time and provides methods to find the replay time
 * for a moment in the race.
 *
 * @author Leonard
 */
public class ReplayOffsetExtension
        implements AccClientExtension, EventListener {

    /**
     * Explaination of how this works:
     *
     * The replay in the game resets whenever the session changes or when the
     * game first connects.
     *
     * We can find the `sessionChangeTime` by listening for the sessionChange
     * event and then saving the current system timestamp. Initialisation events
     * are beeing ignored.
     *
     * We can find the time when the game conntected by listening for
     * Broadcasting events. They include a timer for how long the game has
     * connected to the server. We can then calculate the `gameConnectionTime`
     * by subtracting that timer from the current system timestamtp.
     *
     * Finding the replay start timestamp is split into three cases:
     *
     * CASE 1: - The `sessionChangeTime` is known. This implies that the game
     * and this client were already connected when the session changed.
     * Theirfore the replay will start when the session changed.
     *
     * CASE 2: - The `sessionChangeTime` is not known. - The
     * `gameConnectionTime` is known. Because of the inherint delay between
     * connecting the game to the server and connecting this client to the game,
     * we cannot be sure that there was not a session change between the game
     * connection and the client connection. We can get some insite however by
     * estimating the latest possible timestamp for a session change by looking
     * at the current session time and subtracting that from the current system
     * timestamp. Because the session has been running for x amount of time, the
     * last session change cannot have happened less that x amount of time ago.
     * If the game connected less that x amount of time ago then we can use
     * `gameConnectionTime` as our replay start time. Otherwise we cannot be
     * sure and have to find the replay star time manually with CASE 3.
     *
     * CASE 3: - Replay start time cannot be determined by CASE 1 or CASE 2. or
     * - The `sessionChangeTime` is not known. - The `gameConnectionTime` is not
     * known. Start an instant replay at t ms ago for 1 second. If we get a
     * RealtimeUpdate with the replay status then t is in the replay. If we dont
     * get an Update with the replay status then t is not in the replay. Binary
     * search to find a t that is less than 5 seconds afte the replay has
     * started. Save that t as the replay start time.
     */
    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(ReplayOffsetExtension.class.getName());

    /**
     * The timestamp of when the replay starts.
     */
    private static long replayStartTime = 0;
    /**
     * Timestamp of when the game connected to the server.
     */
    private static long gameConnectionTime = 0;
    /**
     * The replay seams to be offset from the time calculated here by roughly
     * this ammount.
     */
    private static int magicOffset = -6000;

    public ReplayOffsetExtension() {
        EventBus.register(this);
    }

    @Override
    public LPContainer getPanel() {
        return null;
    }

    @Override
    public void removeExtension() {
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            SessionChanged event = (SessionChanged) e;
            if (!event.isInitialisation()) {
                replayStartTime = System.currentTimeMillis();
                LOG.info("Setting replayStartTime based on session change time to: " + replayStartTime);
                EventBus.publish(new ReplayStart());
            }
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();

            if (replayStartTime == 0) {
                long now = System.currentTimeMillis();
                gameConnectionTime = now - event.getTimeMs();
                long latestSessionChange = now - (int) Main.getClient().getModel().getSessionInfo().getSessionTime();

                if (gameConnectionTime > latestSessionChange) {
                    replayStartTime = gameConnectionTime;
                    LOG.info("Setting replayStartTime based on game connection time to: " + gameConnectionTime);
                    EventBus.publish(new ReplayStart());
                }

            }
        }
    }

    /**
     * Gets the replay time based on a point in the session.
     *
     * @param sessionTime The session time in ms of the moment to find the
     * replay time for.
     * @return The time in the replay. Negative if the moment is not in the
     * replay or if the replay time is now know.
     */
    public static int getReplayTimeFromSessionTime(int sessionTime) {
        if (!isReplayTimeKnown()) {
            return -1;
        }
        long now = System.currentTimeMillis();
        long sessionOffset = (now - replayStartTime)
                - (int) Main.getClient().getModel().getSessionInfo().getSessionTime();
        return sessionTime + (int) sessionOffset + magicOffset;
    }

    /**
     * Gets the replay time based on a time since the game connection started.
     *
     * @param timeSinceConnection the time since the game connection started in
     * ms.
     * @return The time in the replay. Negative if the moment is not in the
     * replay or if the replay time is now know.
     */
    public static int getReplayTimeFromConnectionTime(int timeSinceConnection) {
        if (!isReplayTimeKnown()) {
            return -1;
        }
        if (gameConnectionTime == 0) {
            return -1;
        }

        long now = System.currentTimeMillis();
        long requestTimeStamp = timeSinceConnection + gameConnectionTime;
        long sessionStartTimeStamp = now
                - (int) Main.getClient().getModel().getSessionInfo().getSessionTime();
        long sessionOffset = (now - replayStartTime)
                - (int) Main.getClient().getModel().getSessionInfo().getSessionTime();
        return (int) (requestTimeStamp - sessionStartTimeStamp + sessionOffset) + magicOffset;
    }

    /**
     * Returns true if the replay time is known. If false then the replay time
     * has to be calculated. This can be done with the `findReplayOffset`
     * method.
     *
     * @return True if the replay time is known.
     */
    public static boolean isReplayTimeKnown() {
        return (replayStartTime != 0);
    }

    /**
     * When this method is called it begins the process of finding the correct
     * offset for the replay. This process may take a while and should only be
     * started when the user is aware of it.
     */
    public static void findSessionChange() {
        if (isReplayTimeKnown()) {
            return;
        }

    }
}
