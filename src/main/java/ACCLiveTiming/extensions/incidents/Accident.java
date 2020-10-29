/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.networking.data.CarInfo;
import static ACCLiveTiming.networking.enums.SessionType.PRACTICE;
import static ACCLiveTiming.networking.enums.SessionType.QUALIFYING;
import static ACCLiveTiming.networking.enums.SessionType.RACE;
import ACCLiveTiming.utility.TimeUtils;
import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class Accident extends LPTable.Entry {

    private static Logger LOG = Logger.getLogger(Accident.class.getName());

    /**
     * time of the earliest accident event.
     */
    private final float earliestTime;
    /**
     * time of the latest accident event.
     */
    private final float latestTime;
    /**
     * List of cars involved by carID.
     */
    private final List<CarInfo> cars;
    /**
     * System timestamp for this accident.
     */
    private final long timestamp;
    /**
     * The session index when it occured.
     */
    private final SessionId sessionID;
    /**
     * The number of this accident.
     */
    private final int incidentNumber;

    public static final LPTable.Renderer numberRenderer = new LPTable.Renderer() {
        @Override
        public void draw(LPTable.Entry entry) {
            switch (((Accident) entry).getSessionID().getType()) {
                case PRACTICE:
                    applet.fill(LookAndFeel.COLOR_PRACTICE);
                    break;
                case QUALIFYING:
                    applet.fill(LookAndFeel.COLOR_QUALIFYING);
                    break;
                case RACE:
                    applet.fill(LookAndFeel.COLOR_RACE);
                    break;

            }
            applet.rect(1, 1, width - 2, height - 2);
            applet.fill(255);
            applet.textAlign(CENTER, CENTER);
            applet.text(String.valueOf(((Accident) entry).getIncidentNumber()),
                    width / 2f, height / 2f);
        }
    };
    public static final Function<Accident, String> getTime
            = (a) -> TimeUtils.asDuration(a.getEarliestTime());

    public Accident(float time, CarInfo car, SessionId sessionID) {
        this(time, time, Arrays.asList(car), System.currentTimeMillis(), sessionID, 0);
    }

    public Accident(float time, SessionId sessionId) {
        this(time, time, new LinkedList<CarInfo>(), System.currentTimeMillis(), sessionId, 0);
    }

    private Accident(float earliestTime, float latestTime, List<CarInfo> cars,
            long timestamp, SessionId sessionID, int incidentNumber) {
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.cars = cars;
        this.timestamp = timestamp;
        this.sessionID = sessionID;
        this.incidentNumber = incidentNumber;
    }

    public Accident addCar(float time, CarInfo car, long timestamp) {
        List<CarInfo> c = new LinkedList<>();
        c.addAll(cars);
        c.add(car);
        return new Accident(earliestTime,
                time,
                c,
                timestamp,
                sessionID,
                incidentNumber);
    }

    public Accident withIncidentNumber(int incidentNumber) {
        return new Accident(earliestTime,
                latestTime,
                cars,
                timestamp,
                sessionID,
                incidentNumber);
    }

    public float getEarliestTime() {
        return earliestTime;
    }

    public float getLatestTime() {
        return latestTime;
    }

    public List<CarInfo> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SessionId getSessionID() {
        return sessionID;
    }

    public int getIncidentNumber() {
        return incidentNumber;
    }

}
