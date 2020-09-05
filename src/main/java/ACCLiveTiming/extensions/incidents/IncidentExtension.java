/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.extensions.AccClientExtension;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.networking.data.AccBroadcastingData;
import ACCLiveTiming.networking.data.BroadcastingEvent;
import ACCLiveTiming.utility.SpreadSheetService;
import ACCLiveTiming.utility.TimeUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class IncidentExtension extends AccClientExtension {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());

    /**
     * Incident counter for the different sessions.
     */
    private static Map<SessionId, Integer> incidentCounter = new HashMap<>();

    /**
     * Last accident that is waiting to be commited.
     */
    private Accident stagedAccident = null;
    /**
     * List of accidents that have happened.
     */
    private List<Accident> accidents = new LinkedList<>();

    public IncidentExtension() {
        this.panel = new IncidentPanel(this);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }

    public List<Accident> getAccidents() {
        return Collections.unmodifiableList(accidents);
    }

    @Override
    public void afterPacketReceived(byte type) {
        if (stagedAccident != null) {
            long now = System.currentTimeMillis();
            if (now - stagedAccident.getTimestamp() > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = null;
            }
        }
    }

    @Override
    public void onAccident(BroadcastingEvent event) {
        String logMessage = "Accident: #" + client.getModel().getCar(event.getCarId()).getCarNumber()
                + "\t" + TimeUtils.asDuration(client.getModel().getSessionInfo().getSessionTime());
        client.log(logMessage);
        LOG.info(logMessage);

        float sessionTime = client.getModel().getSessionInfo().getSessionTime();
        SessionId sessionId = client.getSessionId();
        if (stagedAccident == null) {
            stagedAccident = new Accident(sessionTime,
                    event.getCarId(),
                    System.currentTimeMillis(),
                    sessionId,
                    getAndIncrementCounter(sessionId));
        } else {
            float timeDif = stagedAccident.getLatestTime() - sessionTime;
            if (timeDif > 1000) {
                commitAccident(stagedAccident);
                stagedAccident = new Accident(sessionTime,
                        event.getCarId(),
                        System.currentTimeMillis(),
                        sessionId,
                        getAndIncrementCounter(sessionId));
            } else {
                stagedAccident = stagedAccident.addCar(sessionTime,
                        event.getCarId(), System.currentTimeMillis());
            }
        }
    }

    private void commitAccident(Accident a) {
        List<Accident> newAccidents = new LinkedList<>();
        newAccidents.addAll(accidents);
        newAccidents.add(a);
        accidents = newAccidents;

        if (SpreadSheetService.isEnabled()) {
            //sendAccidentToSpreadSheet(a);
        }
    }

    private int getAndIncrementCounter(SessionId sessionId) {
        int result = incidentCounter.getOrDefault(sessionId, 0);
        incidentCounter.put(sessionId, result + 1);
        return result;
    }
}
