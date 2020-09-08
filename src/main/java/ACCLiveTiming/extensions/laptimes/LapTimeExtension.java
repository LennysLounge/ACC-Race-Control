/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.laptimes;

import ACCLiveTiming.client.AccClientExtension;
import ACCLiveTiming.client.SessionId;
import ACCLiveTiming.extensions.incidents.IncidentExtension;
import ACCLiveTiming.networking.data.CarInfo;
import ACCLiveTiming.networking.data.LapInfo;
import ACCLiveTiming.networking.data.RealtimeInfo;
import ACCLiveTiming.networking.enums.LapType;
import ACCLiveTiming.utility.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class LapTimeExtension extends AccClientExtension {

    private static Logger LOG = Logger.getLogger(IncidentExtension.class.getName());

    private final Map<Integer, Integer> lapCount = new HashMap<>();

    private File dir;

    private File logFile;

    public LapTimeExtension() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //create folder for this event.
        dir = new File("laps/" + dateFormat.format(now));
        boolean success = dir.mkdir();
        if (!success) {
            LOG.warning("Error creating the laps directory.");
        }
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        if (lapCount.containsKey(info.getCarId())) {
            if (lapCount.get(info.getCarId()) != info.getLaps()) {
                LapInfo lap = info.getLastLap();
                boolean isPersonalBest = lap.getLapTimeMS() == info.getBestSessionLap().getLapTimeMS();
                boolean isSessionBest = lap.getLapTimeMS() == client.getModel().getSessionInfo().getBestSessionLap().getLapTimeMS();
                onLapComplete(lap, isPersonalBest, isSessionBest);
            }
        }
        lapCount.put(info.getCarId(), info.getLaps());
    }

    private void onLapComplete(LapInfo lap, boolean isPB, boolean isSB) {
        CarInfo car = client.getModel().getCar(lap.getCarId());

        String message = "Lap completed: #" + car.getCarNumber()
                + "\t" + TimeUtils.asLapTime(lap.getLapTimeMS()) + "\t";
        if (isPB) {
            message += "[Personal Best]";
        }
        if (isSB) {
            message += "[Session Best]";
        }
        if (lap.getType() == LapType.INLAP) {
            message += "[Inlap]";
        }
        if (lap.getType() == LapType.OUTLAP) {
            message += "[Outlap]";
        }
        client.log(message);
        LOG.info(message);
    }

    @Override
    public void onSessionChanged(SessionId oldId, SessionId newId) {
        //Create new log file for this session
        logFile = new File(dir.getAbsolutePath() + "/" + newId.getType().name() + "_" + newId.getNumber() + ".csv");
        try {
            logFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(LapTimeExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

}
