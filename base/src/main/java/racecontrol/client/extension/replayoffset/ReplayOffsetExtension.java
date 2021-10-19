/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.replayoffset;

import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.events.SessionChangedEvent;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.events.ConnectionOpenedEvent;

/**
 * Calculates the replay start time and provides methods to find the replay time
 * for a moment in the race.
 *
 * @author Leonard
 */
public class ReplayOffsetExtension
        implements EventListener, AccBroadcastingExtension {

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
     * Singelton instance.
     */
    private static ReplayOffsetExtension instance;
    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(ReplayOffsetExtension.class.getName());
    /**
     * Reference to the connection client.
     */
    private final AccBroadcastingClient client;

    /**
     * The timestamp of when the replay starts.
     */
    private long replayStartTime = 0;
    /**
     * Timestamp of when the game connected to the server.
     */
    private long gameConnectionTime = 0;
    /**
     * The replay seams to be offset from the time calculated here by roughly
     * this ammount.
     */
    private final int MAGIC_OFFSET = -6000;
    /**
     * Flag that incidates that the extension is currently searching for the
     * replay start time.
     */
    private boolean isInSearchMode = false;
    /**
     * The delay for the replay.
     */
    private int replayDelay = 0;
    /**
     * Maximum allowed delay for the replay start. After this amount of time the
     * replay is declared to never start.
     */
    private final int ALLOWED_REPLAY_DELAY = 1000;
    /**
     * True indicates that we have to wait for the replay to finish.
     */
    private boolean waitForReplayToFinish = false;
    /**
     * The step in the search process.
     */
    private int searchStep = 0;
    /**
     * Step size for the search.
     */
    private int searchStepSize = 1000;

    private long lowerBound = 0;
    private long upperBound = 0;

    public static ReplayOffsetExtension getInstance() {
        if (instance == null) {
            instance = new ReplayOffsetExtension();
        }
        return instance;
    }

    private ReplayOffsetExtension() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
        replayStartTime = 0;
        gameConnectionTime = 0;
        isInSearchMode = false;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ConnectionOpenedEvent) {
            //reset state
            replayStartTime = 0;
            gameConnectionTime = 0;
            isInSearchMode = false;
        } else if (e instanceof SessionChangedEvent) {
            SessionChangedEvent event = (SessionChangedEvent) e;
            if (!event.isInitialisation()) {
                replayStartTime = System.currentTimeMillis();
                LOG.info("Setting replayStartTime based on session change time to: " + replayStartTime);
                EventBus.publish(new ReplayStartKnownEvent());
            }
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();

            if (replayStartTime == 0) {
                long now = System.currentTimeMillis();
                gameConnectionTime = now - event.getTimeMs();
                long latestSessionChange = now - (int) client.getModel().getSessionInfo().getSessionTime();

                if (gameConnectionTime > latestSessionChange) {
                    replayStartTime = gameConnectionTime;
                    LOG.info("Setting replayStartTime based on game connection time to: " + gameConnectionTime);
                    EventBus.publish(new ReplayStartKnownEvent());
                } else {
                    EventBus.publish(new ReplayStartRequiresSearchEvent());
                }
            }
        } else if (e instanceof RealtimeUpdateEvent) {
            SessionInfo info = ((RealtimeUpdateEvent) e).getSessionInfo();
            if (isInSearchMode) {

                if (info.isReplayPlaying()) {
                    waitForReplayToFinish = true;
                } else {
                    replayDelay += client.getUpdateInterval();
                    if (replayDelay > ALLOWED_REPLAY_DELAY) {
                        searchStep(true);
                    }
                }

                if (waitForReplayToFinish) {
                    if (!info.isReplayPlaying()) {
                        searchStep(false);
                        waitForReplayToFinish = false;
                    }
                }
            }
        } else if (e instanceof ReplayStartKnownEvent) {
            searchStep = 0;
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
    public int getReplayTimeFromSessionTime(int sessionTime) {
        if (!isReplayTimeKnown()) {
            return -1;
        }

        long now = System.currentTimeMillis();
        long sessionOffset = (now - replayStartTime)
                - (int) client.getModel().getSessionInfo().getSessionTime();
        return sessionTime + (int) sessionOffset + MAGIC_OFFSET;
    }

    /**
     * Gets the replay time based on a time since the game connection started.
     *
     * @param timeSinceConnection the time since the game connection started in
     * ms.
     * @return The time in the replay. Negative if the moment is not in the
     * replay or if the replay time is now know.
     */
    public int getReplayTimeFromConnectionTime(int timeSinceConnection) {
        if (!isReplayTimeKnown()) {
            return -1;
        }
        if (gameConnectionTime == 0) {
            return -1;
        }

        long now = System.currentTimeMillis();
        long requestTimeStamp = timeSinceConnection + gameConnectionTime;
        long sessionStartTimeStamp = now
                - (int) client.getModel().getSessionInfo().getSessionTime();
        long sessionOffset = (now - replayStartTime)
                - (int) client.getModel().getSessionInfo().getSessionTime();
        return (int) (requestTimeStamp - sessionStartTimeStamp + sessionOffset) + MAGIC_OFFSET;
    }

    /**
     * Returns true if the replay time is known. If false then the replay time
     * has to be calculated. This can be done with the `findReplayOffset`
     * method.
     *
     * @return True if the replay time is known.
     */
    public boolean isReplayTimeKnown() {
        return (replayStartTime != 0);
    }

    /**
     * Returns true if a search for the replay time is needed.
     *
     * @return True if a search for replay time is needed.
     */
    public boolean requireSearch() {
        return replayStartTime == 0 && gameConnectionTime != 0;
    }

    /**
     * When this method is called it begins the process of finding the correct
     * offset for the replay. This process may take a while and should only be
     * started when the user is aware of it.
     */
    public void findSessionChange() {
        if (isReplayTimeKnown()) {
            return;
        }
        EventBus.publish(new ReplayOffsetSearchStartedEvent());
        searchStepSize = 300000;
        searchStep = 1;
        upperBound = System.currentTimeMillis() + searchStepSize;
        lowerBound = upperBound - searchStepSize;
        searchStep(false);
    }

    public boolean isSearching() {
        return searchStep != 0;
    }

    private void searchStep(boolean replayMissed) {
        isInSearchMode = false;
        if (searchStep == 1) {
            if (!replayMissed) {
                //replay was played so we move the bounds and try playing a replay.
                upperBound -= searchStepSize;
                lowerBound = upperBound - searchStepSize;
                playReplayAtTimeStamp(lowerBound);
            } else {
                //replay was missed so we now know the bounds and move onto the 
                //next step
                searchStep = 2;
                playReplayAtTimeStamp(lowerBound / 2 + upperBound / 2);
            }
        } else if (searchStep == 2) {
            long middle = lowerBound / 2 + upperBound / 2;
            //adjust bounds based on if the replay was missed.
            if (replayMissed) {
                lowerBound = middle;
            } else {
                upperBound = middle;
            }

            if ((upperBound - lowerBound) < 1000) {
                searchStep = 3;
            } else {
                middle = lowerBound / 2 + upperBound / 2;
                playReplayAtTimeStamp(middle);
            }
        }
        if (searchStep == 3) {
            long middle = lowerBound / 2 + upperBound / 2;
            replayStartTime = middle;
            LOG.info("Setting replay time based on search algorithm to: " + middle);
            EventBus.publish(new ReplayStartKnownEvent());
            searchStep = 0;
        }
    }

    private void playReplayAtTimeStamp(long timestamp) {
        replayDelay = 0;
        isInSearchMode = true;

        long now = System.currentTimeMillis();
        int secondsBack = ((int) (now - timestamp)) / 1000;
        client.sendInstantReplayRequestSimple(secondsBack, 1);
    }
}
