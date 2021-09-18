/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.virtualsafetycar.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.SessionId;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class VirtualSafetyCarController
        implements EventListener {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(VirtualSafetyCarController.class.getName());
    /**
     * Singelton instance.
     */
    private static VirtualSafetyCarController instance;
    /**
     * Reference to the game client.
     */
    private final AccBroadcastingClient client;
    /**
     * Current session id.
     */
    private SessionId sessionId;
    /**
     * Indicates that the virtual safety car is on.
     */
    private boolean vscOn = false;
    /**
     * Speed limit for the vsc.
     */
    private int speedLimit = 0;
    /**
     * Speed tolerance for the vsc.
     */
    private int speedTolerance = 0;
    /**
     * Time tolerance for the vsc.
     */
    private int timeTolerance = 0;
    /**
     * Map holds all cars that are currently over the limit. Maps carId to
     * VSCRecord.
     */
    private final Map<Integer, VSCRecord> carsOverTheLimit = new HashMap<>();

    public static VirtualSafetyCarController getInstance() {
        if (instance == null) {
            instance = new VirtualSafetyCarController();
        }
        return instance;
    }

    private VirtualSafetyCarController() {
        EventBus.register(this);
        client = AccBroadcastingClient.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (vscOn) {
            if (e instanceof RealtimeCarUpdateEvent) {
                onRealtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
            } else if (e instanceof SessionChangedEvent) {
                onSessionChanged((SessionChangedEvent) e);
            }
        }
    }

    private void onRealtimeCarUpdate(RealtimeInfo info) {
        if (info.getKMH() > speedLimit + speedTolerance) {

            float sessionNow = client.getModel().getSessionInfo().getSessionTime();
            // get or create record.
            VSCRecord record = carsOverTheLimit.getOrDefault(info.getCarId(),
                    new VSCRecord(info.getCarId(),
                            sessionNow,
                            0));

            // Set speed over.
            int speedOver = info.getKMH() - speedLimit;
            if (speedOver > record.speedOver) {
                record.speedOver = speedOver;
            }
            // update time over.
            record.timeOver = sessionNow - record.sessionTimeStamp;

            // write record back
            carsOverTheLimit.put(info.getCarId(), record);
        } else {
            if (carsOverTheLimit.containsKey(info.getCarId())) {
                if (carsOverTheLimit.get(info.getCarId()).timeOver > timeTolerance) {
                    // publish VSC violation.
                    commitViolation(info.getCarId());
                    carsOverTheLimit.remove(info.getCarId());
                }
            }
        }
    }

    private void onSessionChanged(SessionChangedEvent e) {
        stopVSC();
        sessionId = e.getSessionId();
    }

    private void commitViolation(int carId) {
        VSCRecord record = carsOverTheLimit.get(carId);

        String logText = String.format("VSC violation by car #%d \t+%d kmh \t%s s",
                client.getModel().getCar(carId).getCarNumber(),
                record.speedOver,
                TimeUtils.asDelta(record.timeOver));
        
        LOG.info(logText);
        UILogger.log(logText);

        EventBus.publish(new VSCViolationEvent(record.carId,
                record.speedOver,
                record.timeOver,
                sessionId,
                record.sessionTimeStamp));
    }

    private void clearAndCommitViolations() {
        for (VSCRecord record : carsOverTheLimit.values()) {
            if (record.timeOver > timeTolerance) {
                commitViolation(record.carId);
            }
        }
        carsOverTheLimit.clear();
    }

    public void startVSC(int speedLimit, int speedTolerance, int timeTolerance) {
        if (client.isConnected()) {
            this.speedLimit = speedLimit;
            this.speedTolerance = speedTolerance;
            this.timeTolerance = timeTolerance;
            float sessionTime = client.getModel().getSessionInfo().getSessionTime();
            vscOn = true;

            String logText = "VSC started at " + TimeUtils.asDuration(sessionTime)
                    + " with a speed limit of " + speedLimit
                    + " tolerances (" + speedTolerance + "kmh, " + this.timeTolerance + "s)";
            LOG.info(logText);
            UILogger.log(logText);
            EventBus.publish(new VSCStartEvent(speedLimit,
                    speedTolerance,
                    timeTolerance,
                    sessionId,
                    sessionTime));
        }
    }

    public void stopVSC() {
        vscOn = false;

        String logText = "VSC Stopped";
        LOG.info(logText);
        UILogger.log(logText);
        //publish all current violations.
        clearAndCommitViolations();
        EventBus.publish(new VSCEndEvent(sessionId, client.getModel().getSessionInfo().getSessionTime()));
    }

    public boolean isActive() {
        return vscOn;
    }

    private class VSCRecord {

        public final int carId;
        public final float sessionTimeStamp;
        public int speedOver;
        public float timeOver;

        public VSCRecord(int carId, float sessionTimeStamp, int speedOver) {
            this.carId = carId;
            this.sessionTimeStamp = sessionTimeStamp;
            this.speedOver = speedOver;
        }
    }

}
