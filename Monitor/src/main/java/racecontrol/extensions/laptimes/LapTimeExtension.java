/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.laptimes;

import racecontrol.client.data.SessionId;
import racecontrol.client.events.RealtimeCarUpdate;
import racecontrol.eventbus.Event;
import racecontrol.client.extension.AccClientExtension;
import racecontrol.extensions.incidents.IncidentExtension;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.LapInfo;
import racecontrol.client.data.RealtimeInfo;
import racecontrol.client.data.enums.LapType;
import racecontrol.client.events.SessionChanged;
import racecontrol.utility.TimeUtils;
import racecontrol.lpgui.gui.LPContainer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.logging.UILogger;

/**
 *
 * @author Leonard
 */
public class LapTimeExtension
        extends AccClientExtension {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(IncidentExtension.class.getName());
    /**
     * Counts the laps for each car
     */
    private final Map<Integer, Integer> lapCount = new HashMap<>();
    /**
     * Directory where the files are in
     */
    private File dir;
    /**
     * current log file
     */
    private File logFile;
    /**
     * Lists of lap times. Maps car ids to a list of lap times
     */
    private final Map<Integer, List<Integer>> laps = new HashMap<>();
    /**
     * Maps car ids to their row in the log file.
     */
    private final Map<Integer, Integer> rows = new HashMap<>();
    /**
     * Counts how many rows are needed.
     */
    private int rowCounter = 0;
    /**
     * Is the logging for this extension enabled.
     */
    private final boolean isLoggingEnabled;

    public LapTimeExtension(AccBroadcastingClient client, boolean isLoggingEnabled) {
        super(client);
        this.isLoggingEnabled = isLoggingEnabled;
        if (isLoggingEnabled) {
            createFolder();
        }
    }

    private void createFolder() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //create folder for this event.
        dir = new File("log/laps_" + dateFormat.format(now));
        boolean success = dir.mkdir();
        if (!success) {
            LOG.warning("Error creating the laps directory.");
        }
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdate) {
            onRealtimeCarUpdate(((RealtimeCarUpdate) e).getInfo());
        } else if (e instanceof SessionChanged) {
            onSessionChanged(((SessionChanged) e).getSessionId());
        }

    }

    public void onRealtimeCarUpdate(RealtimeInfo info) {
        if (lapCount.containsKey(info.getCarId())) {
            if (lapCount.get(info.getCarId()) != info.getLaps()) {
                lapCount.put(info.getCarId(), info.getLaps());
                LapInfo lap = info.getLastLap();
                boolean isPersonalBest = lap.getLapTimeMS() == info.getBestSessionLap().getLapTimeMS();
                boolean isSessionBest = lap.getLapTimeMS() == getClient().getModel().getSessionInfo().getBestSessionLap().getLapTimeMS();
                onLapComplete(lap, isPersonalBest, isSessionBest);
            }
        } else {
            lapCount.put(info.getCarId(), info.getLaps());
        }
    }

    private void onLapComplete(LapInfo lap, boolean isPB, boolean isSB) {
        CarInfo car = getClient().getModel().getCar(lap.getCarId());

        boolean isFirstLap = lapCount.get(lap.getCarId()) == 1;
        int lapNr = lapCount.get(lap.getCarId());

        if (!laps.containsKey(car.getCarId())) {
            laps.put(car.getCarId(), new LinkedList<>());
            rows.put(car.getCarId(), rowCounter++);
        }

        if (!isFirstLap && lap.getType() == LapType.REGULAR) {
            laps.get(car.getCarId()).add(lap.getLapTimeMS());
            if (isLoggingEnabled) {
                printLapToFile();
            }
        }

        String message = "Lap completed: #" + car.getCarNumber()
                + "\t" + TimeUtils.asLapTime(lap.getLapTimeMS()) + "\t";
        if (isFirstLap) {
            message += "[Lap 1]";
        }
        if (isPB) {
            message += "[PB]";
        }
        if (isSB) {
            message += "[SB]";
        }
        if (lap.getType() == LapType.INLAP) {
            message += "[Inlap]";
        }
        if (lap.getType() == LapType.OUTLAP) {
            message += "[Outlap]";
        }

        UILogger.log(message);
        LOG.info(message);
    }

    private void printLapToFile() {
        try ( PrintWriter writer = new PrintWriter(logFile)) {

            for (Entry<Integer, List<Integer>> entry : laps.entrySet()) {
                CarInfo car = getClient().getModel().getCar(entry.getKey());
                writer.print(car.getCarNumber());
                writer.print("," + car.getDriver().getFirstName() + " " + car.getDriver().getLastName());
                for (int lap : entry.getValue()) {
                    writer.print("," + lap);
                }
                writer.println();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LapTimeExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onSessionChanged(SessionId newId) {
        if (isLoggingEnabled) {
            if (logFile != null) {
                printLapToFile();
            }
        }

        laps.clear();
        //Set lap counts to 0
        lapCount.forEach((key, count) -> lapCount.put(key, 0));

        if (isLoggingEnabled) {
            logFile = new File(dir.getAbsolutePath() + "/" + newId.getType().name() + "_" + newId.getNumber() + ".csv");
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Error creating laps log file.", e);
            }
        }

    }

    @Override
    public LPContainer getPanel() {
        return null;
    }
}
