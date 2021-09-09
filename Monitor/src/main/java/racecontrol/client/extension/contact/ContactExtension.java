/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.contact;

import racecontrol.client.data.SessionId;
import racecontrol.client.events.AfterPacketReceived;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.enums.BroadcastingEventType;
import racecontrol.utility.TimeUtils;
import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.client.AccBroadcastingClient;
import java.util.logging.Logger;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.data.CarInfo;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;

/**
 * This extension listens for contact between cars during a session and creates
 * a ContactEvent for them.
 *
 * @author Leonard
 */
public class ContactExtension
        implements EventListener {
    /**
     * Singelton instance.
     */
    private static ContactExtension instance = null;

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(ContactExtension.class.getName());
    /**
     * Last accident that is waiting to be commited.
     */
    private ContactInfo stagedAccident = null;
    /**
     * Reference to the replay offset extension
     */
    private final ReplayOffsetExtension replayOffsetExtension;
    
    public static ContactExtension getInstance(){
        if(instance == null){
            instance = new ContactExtension();
        }
        return instance;
    }

    private ContactExtension() {
        EventBus.register(this);
        replayOffsetExtension = AccBroadcastingClient.getClient().getOrCreateExtension(ReplayOffsetExtension.class);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof AfterPacketReceived) {
            afterPacketReceived(((AfterPacketReceived) e).getType());
        } else if (e instanceof BroadcastingEventEvent) {
            BroadcastingEvent event = ((BroadcastingEventEvent) e).getEvent();
            if (event.getType() == BroadcastingEventType.ACCIDENT) {
                onAccident(event);
            }
        }
    }

    public void afterPacketReceived(byte type) {
        if (stagedAccident != null) {
            long now = System.currentTimeMillis();
            if (now - stagedAccident.getSystemTimestamp() > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = null;
            }
        }
    }

    public void onAccident(BroadcastingEvent event) {
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        String logMessage = "Accident: #" + getClient().getModel().getCar(event.getCarId()).getCarNumber()
                + "\t\t" + TimeUtils.asDuration(sessionTime)
                + "\t" + TimeUtils.asDuration(replayOffsetExtension.getReplayTimeFromConnectionTime(event.getTimeMs()));
        UILogger.log(logMessage);
        LOG.info(logMessage);

        SessionId sessionId = getClient().getSessionId();
        if (stagedAccident == null) {
            stagedAccident = new ContactInfo(sessionTime,
                    replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                    getClient().getModel().getCar(event.getCarId()),
                    sessionId);
        } else {
            float timeDif = stagedAccident.getSessionLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new ContactInfo(sessionTime,
                        replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                        getClient().getModel().getCar(event.getCarId()),
                        sessionId);
            } else {
                stagedAccident = stagedAccident.addCar(sessionTime,
                        getClient().getModel().getCar(event.getCarId()),
                        System.currentTimeMillis());
            }
        }
    }

    public void addEmptyAccident() {
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        commitAccident(new ContactInfo(sessionTime,
                replayOffsetExtension.isReplayTimeKnown() ? replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime) : 0,
                getClient().getSessionId()));
    }

    private void commitAccident(ContactInfo a) {
        EventBus.publish(new ContactEvent(a));
    }

    public void findReplayOffset() {
        replayOffsetExtension.findSessionChange();
    }

    public void createDummyIncident() {
        if (getClient().getModel().getCarsInfo().isEmpty()) {
            return;
        }
        LOG.info("Create dummy accident.");
        int nCars = (int) Math.floor(Math.random() * Math.min(6, getClient().getModel().getCarsInfo().size()) + 1);
        float sessionTime = getClient().getModel().getSessionInfo().getSessionTime();
        ContactInfo incident = new ContactInfo(
                sessionTime,
                replayOffsetExtension.getReplayTimeFromSessionTime((int) sessionTime),
                getClient().getSessionId());
        for (int i = 0; i < nCars; i++) {
            incident = incident.addCar(
                    sessionTime,
                    getRandomCar(),
                    0);
        }
        commitAccident(incident);
    }

    private CarInfo getRandomCar() {
        int r = (int) Math.floor(Math.random() * getClient().getModel().getCarsInfo().size());
        int i = 0;
        for (CarInfo car : getClient().getModel().getCarsInfo().values()) {
            if (i++ == r) {
                return car;
            }
        }
        return null;
    }

    public void focusOnCar(int carId) {
        LOG.info("Setting focus on car " + carId);
        getClient().sendChangeFocusRequest(carId);
    }

    public void startAccidentReplay(ContactInfo incident, int seconds) {
        LOG.info("Starting instant replay for incident for " + seconds);
        getClient().sendInstantReplayRequestWithCamera(
                incident.getSessionEarliestTime() - seconds * 1000f - 5000,
                seconds + 5,
                getClient().getModel().getSessionInfo().getFocusedCarIndex(),
                getClient().getModel().getSessionInfo().getActiveCameraSet(),
                getClient().getModel().getSessionInfo().getActiveCamera()
        );
    }

}
