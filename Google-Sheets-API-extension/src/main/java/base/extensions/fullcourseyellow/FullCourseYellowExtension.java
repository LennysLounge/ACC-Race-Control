/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.fullcourseyellow;

import base.extensions.fullcourseyellow.events.FCYStart;
import base.extensions.fullcourseyellow.events.FCYStop;
import base.extensions.fullcourseyellow.events.FCYViolation;
import base.screen.networking.events.RealtimeCarUpdate;
import base.screen.networking.RealtimeUpdate;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.logging.LoggingExtension;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.utility.TimeUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class FullCourseYellowExtension
        extends AccClientExtension
        implements EventListener {

    private static final Logger LOG = Logger.getLogger(FullCourseYellowExtension.class.getName());

    private CarSpeedTableModel model = new CarSpeedTableModel();

    /**
     * Map from carId to ListEntry.
     */
    private final Map<Integer, CarInfo> entires = new HashMap<>();
    /**
     * Indicates that there is currently a Full course yellow.
     */
    private boolean isFCY = false;
    /**
     * A map to store the last time a FCY incident has been reported to avoid
     * sending an incident for every tick.
     */
    private final Map<Integer, Long> repeatList = new HashMap<>();
    /**
     * Time between sending a repeat incident in ms.
     */
    private final long repeatInterval = 5000;

    public FullCourseYellowExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdate) {
            onRealtimeUpdate(((RealtimeUpdate) e).getSessionInfo());
        } else if (e instanceof RealtimeCarUpdate) {
            onRealtimeCarUpdate(((RealtimeCarUpdate) e).getInfo());
        }
    }

    private void onRealtimeUpdate(SessionInfo sessionInfo) {
        List<CarInfo> sorted = entires.values().stream()
                .filter(entry -> entry.isConnected())
                .sorted((e1, e2) -> compareTo(e1, e2))
                .collect(Collectors.toList());

        model.setEntries(sorted);
    }

    private int compareTo(CarInfo c1, CarInfo c2) {
        return (int) Math.signum(c1.getRealtime().getPosition() - c2.getRealtime().getPosition());
    }

    private void onRealtimeCarUpdate(RealtimeInfo info) {
        CarInfo car = client.getModel().getCarsInfo().getOrDefault(info.getCarId(), new CarInfo());
        entires.put(car.getCarId(), car);
        if (isFCY && info.getKMH() > 50) {
            long lastRepeat = repeatList.getOrDefault(info.getCarId(), 0l);
            long now = System.currentTimeMillis();
            long timeSinceLastRepeat = now - lastRepeat;
            if (timeSinceLastRepeat > repeatInterval) {
                repeatList.put(info.getCarId(), now);
                EventBus.publish(
                        new FCYViolation(client.getModel().getSessionInfo().getSessionTime(),
                                car,
                                info.getKMH())
                );
            }
        }
    }

    public CarSpeedTableModel getTableModel() {
        return model;
    }

    public void setColumnCount(int count) {
        model.setColumnCount(count);
    }

    public void startFCY() {
        if (isFCY) {
            return;
        }
        float timeStamp = client.getModel().getSessionInfo().getSessionTime();
        String time = TimeUtils.asDuration(timeStamp);
        LOG.info("Starting full course yellow at " + time);
        LoggingExtension.log("Starting full course yellow at " + time);
        isFCY = true;
        model.setFCYActive(true);
        EventBus.publish(new FCYStart(timeStamp));
    }

    public void stopFCY() {
        if (!isFCY) {
            return;
        }
        float timeStamp = client.getModel().getSessionInfo().getSessionTime();
        String time = TimeUtils.asDuration(timeStamp);
        LOG.info("Stopping full course yellow at " + time);
        LoggingExtension.log("Stopping full course yellow at " + time);
        isFCY = false;
        model.setFCYActive(false);
        EventBus.publish(new FCYStop(timeStamp));
    }

    public boolean isFCY() {
        return isFCY;
    }

}
