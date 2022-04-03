/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import racecontrol.client.data.SessionId;
import racecontrol.client.events.SessionPhaseChangedEvent;
import racecontrol.eventbus.Event;
import racecontrol.client.data.CarInfo;
import racecontrol.client.data.SessionInfo;
import racecontrol.client.data.enums.SessionPhase;
import racecontrol.client.data.enums.SessionType;
import racecontrol.client.events.CarConnectedEvent;
import racecontrol.client.events.SessionChangedEvent;
import racecontrol.utility.TimeUtils;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.logging.UILogger;
import racecontrol.client.ClientExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnection.State;
import racecontrol.client.extension.replayoffset.ReplayOffsetExtension;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtension extends ClientExtension
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static GoogleSheetsAPIExtension instance;
    /**
     * Referenece to the game client.
     */
    private final AccBroadcastingClient CLIENT;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIExtension.class.getName());
    /**
     * The replay offset extension.
     */
    private final ReplayOffsetExtension REPLAY_OFFSET_EXTENSION;
    /**
     * The current sessionId.
     */
    private SessionId currentSessionId;
    /**
     * The connection to the google sheet.
     */
    private final GoogleSheetsConnection connection;
    /**
     * Holds valid sheet targets. Maps a sheet name to its object.
     */
    private Map<String, Sheet> validSheets = new HashMap<>();

    public static GoogleSheetsAPIExtension getInstance() {
        if (instance == null) {
            instance = new GoogleSheetsAPIExtension();
        }
        return instance;
    }

    private GoogleSheetsAPIExtension() {
        EventBus.register(this);
        REPLAY_OFFSET_EXTENSION = ReplayOffsetExtension.getInstance();
        CLIENT = AccBroadcastingClient.getClient();
        connection = new GoogleSheetsConnection();
    }

    /**
     * Sends an incident to the current targeted spreadsheet.
     *
     * @param time the session time when this incident occured.
     * @param info information about the incident.
     */
    public void sendIncident(int time, String info) {
        connection.sendIncident(TimeUtils.asDuration(time), info);
    }

    public void start(GoogleSheetsConfiguration config) {
        connection.start(config);
        connection.sendCarsConnection(new ArrayList<>(CLIENT.getBroadcastingData().getCarsInfo().values()));
    }

    public void stop() {
        connection.stop();
    }

    public String getSheetTarget() {
        return connection.getSheetTarget();
    }

    public void setSheetTarget(String sheet) {
        if (validSheets.containsKey(sheet)) {
            connection.setSheetTarget(sheet);
        }
    }

    public State getState() {
        return connection.getState();
    }

    public String getSpreadsheetTitle() {
        return connection.getSpreadSheetTitle();
    }

    public Set<String> getValidSheets() {
        return validSheets.keySet();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChangedEvent) {
            updateSpreadsheetTarget(((SessionChangedEvent) e).getSessionId());
        } else if (e instanceof SessionPhaseChangedEvent) {
            updateGreenFlagOffset(((SessionPhaseChangedEvent) e).getSessionInfo());
        } else if (e instanceof CarConnectedEvent) {
            updateCarsConnected(((CarConnectedEvent) e).getCar());
        } else if (e instanceof GoogleSheetsConnectedEvent) {
            findValidSheetTargets(((GoogleSheetsConnectedEvent) e).getSpreadsheet());
            updateSpreadsheetTarget(CLIENT.getModel().currentSessionId);
        }
    }

    private void updateGreenFlagOffset(SessionInfo info) {
        if (info.getSessionType() == SessionType.RACE
                && info.getPhase() == SessionPhase.SESSION
                && REPLAY_OFFSET_EXTENSION.isReplayTimeKnown()) {
            connection.sendGreenFlagOffset(REPLAY_OFFSET_EXTENSION.getReplayTimeFromSessionTime(0));
        }
    }

    private void updateSpreadsheetTarget(SessionId sessionId) {
        currentSessionId = sessionId;
        setSheetTarget(getTargetSheet(currentSessionId));
        LOG.info("Target Sheet changed to \"" + connection.getSheetTarget() + "\"");
        UILogger.log("Spreasheet target changed to \"" + connection.getSheetTarget() + "\"");
    }

    private String getTargetSheet(SessionId sessionId) {
        String session = "Practice";
        if (sessionId.getType() == SessionType.QUALIFYING) {
            session = "Qualifying";
        } else if (sessionId.getType() == SessionType.RACE) {
            session = "Race";
        }
        String preferedTarget = session + " " + String.valueOf(sessionId.getNumber() + 1);
        if (validSheets.containsKey(preferedTarget)) {
            return preferedTarget;
        } else {
            if (validSheets.containsKey(session)) {
                return session;
            } else {
                Optional<String> validSheet = validSheets.keySet().stream().findFirst();
                if (validSheet.isPresent()) {
                    return validSheet.get();
                }
            }
        }
        return "";
    }

    private void updateCarsConnected(CarInfo car) {
        connection.sendCarConnection(car);
    }

    private void findValidSheetTargets(Spreadsheet spreadsheet) {
        validSheets.clear();
        for (Sheet sheet : spreadsheet.getSheets()) {
            String title = sheet.getProperties().getTitle();
            String pattern = "(Race|Practice|Qualifying)( \\d+)?";
            if (title.matches(pattern)) {
                validSheets.put(title, sheet);
            }
        }
    }

}
