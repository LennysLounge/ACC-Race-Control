/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.googlesheetsapi;

import base.extensions.fullcourseyellow.events.FCYStart;
import base.extensions.fullcourseyellow.events.FCYStop;
import base.extensions.fullcourseyellow.events.FCYViolation;
import base.screen.Main;
import base.screen.networking.SessionId;
import base.screen.networking.SessionChanged;
import base.screen.networking.events.SessionPhaseChanged;
import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.extensions.AccClientExtension;
import base.screen.extensions.incidents.IncidentInfo;
import base.screen.extensions.incidents.events.Accident;
import base.screen.extensions.logging.LoggingExtension;
import base.screen.networking.AccBroadcastingClient;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.SessionInfo;
import base.screen.networking.enums.SessionPhase;
import base.screen.networking.enums.SessionType;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.gui.LPContainer;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtension
        implements EventListener, AccClientExtension {

    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIExtension.class.getName());

    /**
     * Sheet service for the connection.
     */
    private static Sheets sheetService;
    /**
     * Id of the connected spreadsheet.
     */
    private String spreadSheetID;
    /**
     * Indicates that the eventLoop is running.
     */
    private boolean running = false;
    /**
     * Event loop.
     */
    private Thread eventLoop;
    /**
     * Event queue.
     */
    private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    /**
     * The current sessionId.
     */
    private SessionId currentSessionId;
    /**
     * Current target sheet.
     */
    private String currentSheetTarget = "";

    private boolean isMeasuringGreenFlagOffset = false;
    private long greenFlagOffsetTimestamp = 0;

    private final GoogleSheetsAPIPanel panel;

    private final AccBroadcastingClient client;

    public GoogleSheetsAPIExtension() {
        EventBus.register(this);
        panel = new GoogleSheetsAPIPanel(this);
        client = Main.getClient();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            currentSessionId = ((SessionChanged) e).getSessionId();
            currentSheetTarget = getTargetSheet(currentSessionId);
            LOG.info("Target Sheet changed to \"" + currentSheetTarget + "\"");
            LoggingExtension.log("Spreasheet target changed to \"" + currentSheetTarget + "\"");
        } else if (e instanceof Accident) {
            IncidentInfo info = ((Accident) e).getInfo();
            String sessionTime = TimeUtils.asDuration(info.getSessionEarliestTime());
            String carNumbers = info.getCars().stream()
                    .map(car -> getCarNumberAndLapCount(car))
                    .collect(Collectors.joining("\n"));
            queue.add(new SendIncidentEvent(sessionTime, carNumbers));
            LOG.info("accident received: " + carNumbers);
        } else if (e instanceof SessionPhaseChanged) {
            SessionInfo info = ((SessionPhaseChanged) e).getSessionInfo();
            if (info.getSessionType() == SessionType.RACE) {
                if (info.getPhase() == SessionPhase.STARTING) {
                    isMeasuringGreenFlagOffset = true;
                    greenFlagOffsetTimestamp = System.currentTimeMillis();
                } else if (info.getPhase() == SessionPhase.SESSION && isMeasuringGreenFlagOffset) {
                    long diff = System.currentTimeMillis() - greenFlagOffsetTimestamp;
                    queue.add(new GreenFlagEvent((int) diff));
                    isMeasuringGreenFlagOffset = false;
                }
            }
        } else if (e instanceof FCYStart) {
            FCYStart event = ((FCYStart) e);
            String sessionTime = TimeUtils.asDuration(event.getSessionTime());
            queue.add(new SendIncidentEvent(sessionTime, "Start FCC"));
        } else if (e instanceof FCYStop) {
            FCYStop event = ((FCYStop) e);
            String sessionTime = TimeUtils.asDuration(event.getSessionTime());
            queue.add(new SendIncidentEvent(sessionTime, "Stop FCC"));
        } else if (e instanceof FCYViolation) {
            /*
            FCYViolation event = ((FCYViolation) e);
            String sessionTime = TimeUtils.asDuration(event.getTimeStamp());
            String message = String.valueOf(event.getCar().getCarNumber())
                    + ", " + String.valueOf(event.getSpeed()) + "km/h";
            queue.add(new SendIncidentEvent(sessionTime, message));
             */
        }
    }

    private String getCarNumberAndLapCount(CarInfo car) {
        return car.getCarNumber() + " [" + (car.getRealtime().getLaps() + 1) + "]";
    }

    private String getTargetSheet(SessionId sessionId) {
        switch (sessionId.getType()) {
            case PRACTICE:
                return "Practice!";
            case QUALIFYING:
                return "Qualifying!";
            case RACE:
                int raceNumber = Math.min(sessionId.getNumber() + 1, 2);
                return "Race " + String.valueOf(raceNumber) + "!";
            default:
                return "Practice!";
        }
    }

    public void sendEmptyIncident() {
        queue.add(new SendIncidentEvent(
                TimeUtils.asDuration(client.getModel().getSessionInfo().getSessionTime()),
                "empty")
        );
    }

    public void start(String spreadSheetURL) throws IllegalArgumentException, RuntimeException {
        //find spreadsheet id with regex
        Pattern p = Pattern.compile(".*/spreadsheets/d/([a-zA-Z0-9-_]+).*");
        Matcher m = p.matcher(spreadSheetURL);
        if (m.matches()) {
            spreadSheetID = m.group(1);
        } else {
            throw new IllegalArgumentException("Url is not a valid google sheets URL.");
        }
        //create sheet service
        try {
            sheetService = createSheetsService();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Error while creating sheet service", e);
        }
        //Start event loop
        running = true;
        eventLoop = new Thread("Sheet service event loop") {
            @Override
            public void run() {
                try {
                    eventLoop();
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Sheet service event loop interrupted.", e);
                }
            }
        };
        eventLoop.start();
    }

    public String getCurrentTargetSheet() {
        return currentSheetTarget;
    }

    public void setCurrentTargetSheet(String sheet) {
        this.currentSheetTarget = sheet;
    }

    public long getGreenFlagTimeStamp() {
        return greenFlagOffsetTimestamp;
    }

    public boolean isGreenFlagOffsetBeeingMeasured() {
        return isMeasuringGreenFlagOffset;
    }

    private void eventLoop() throws InterruptedException {
        while (running) {
            Object o = queue.take();
            if (o instanceof QuitEvent) {
                running = false;
            }
            if (o instanceof SendIncidentEvent) {
                sendIncident((SendIncidentEvent) o);
            }
            if (o instanceof GreenFlagEvent) {
                sendGreenFlag((GreenFlagEvent) o);
            }
        }
    }

    private void sendIncident(SendIncidentEvent event) {

        String sheet = currentSheetTarget;
        String rangeSession = sheet + "B1:D500";

        List<List<Object>> values;
        try {
            values = getCells(rangeSession);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }
        int emptyLine = values.size() + 1;

        rangeSession = sheet + "B" + emptyLine;
        String rangeCars = sheet + "D" + emptyLine;

        List<List<Object>> lineSession = new LinkedList<>();
        lineSession.add(Arrays.asList(event.sessionTime));
        List<List<Object>> lineCars = new LinkedList<>();
        lineCars.add(Arrays.asList(event.carsInvolved));
        try {
            updateCells(rangeSession, lineSession);
            updateCells(rangeCars, lineCars);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error sending spreadsheet values", e);
        }
    }

    private void sendGreenFlag(GreenFlagEvent event) {
        String sheet = currentSheetTarget;
        String range = sheet + "B1:C500";
        List<List<Object>> values;
        try {
            values = getCells(range);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }

        //find row
        int rowNumber = -1;
        for (int i = 0; i < values.size(); i++) {
            for (Object o : values.get(i)) {
                if (((String) o).equals("Greenflag offset:")) {
                    rowNumber = i + 1;
                }
            }
        }
        if (rowNumber < 0) {
            LOG.log(Level.SEVERE, "Green flag offset not found.");
        }

        range = sheet + "C" + rowNumber + ":C" + rowNumber;
        try {
            updateCells(range, Arrays.asList(Arrays.asList(TimeUtils.asDuration(event.time))));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error setting spreadsheet value", e);
            return;
        }
    }

    private Sheets createSheetsService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        //Load client secrets
        final String CREDENTIAL_PATH = "/credentials.json";
        InputStream in = Main.class.getResourceAsStream(CREDENTIAL_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIAL_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
        List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        String TOKENS_DIRECTORY_PATH = "tokens";

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credentials = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Sheets.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName("ACC_AccidentTracker/0.1")
                .build();
    }

    private void updateCells(String range, List<List<Object>> values) throws IOException {
        String valueInputOption = "RAW";
        ValueRange requestBody = new ValueRange();
        requestBody.setRange(range);
        requestBody.setValues(values);
        requestBody.setMajorDimension("ROWS");

        Sheets.Spreadsheets.Values.Update request
                = sheetService.spreadsheets().values().update(spreadSheetID, range, requestBody);
        request.setValueInputOption(valueInputOption);

        UpdateValuesResponse response = request.execute();
    }

    private List<List<Object>> getCells(String range) throws IOException {
        Sheets.Spreadsheets.Values.Get request
                = sheetService.spreadsheets().values().get(spreadSheetID, range);
        request.setValueRenderOption("FORMATTED_VALUE");
        request.setDateTimeRenderOption("FORMATTED_STRING");
        request.setMajorDimension("ROWS");

        ValueRange response = request.execute();

        return response.getValues();
    }

    @Override
    public LPContainer getPanel() {
        return panel;
    }

    private class SendIncidentEvent {

        public String sessionTime;
        public String carsInvolved;

        public SendIncidentEvent(String sessionTime, String carsInvolved) {
            this.sessionTime = sessionTime;
            this.carsInvolved = carsInvolved;
        }
    }

    private class QuitEvent {
    }

    private class GreenFlagEvent {

        public int time;

        public GreenFlagEvent(int time) {
            this.time = time;
        }
    }
}
