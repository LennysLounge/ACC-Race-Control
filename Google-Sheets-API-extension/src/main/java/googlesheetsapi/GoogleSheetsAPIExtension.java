/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googlesheetsapi;

import ACCLiveTiming.monitor.Main;
import ACCLiveTiming.monitor.client.SessionId;
import ACCLiveTiming.monitor.client.events.SessionChanged;
import ACCLiveTiming.monitor.eventbus.Event;
import ACCLiveTiming.monitor.eventbus.EventBus;
import ACCLiveTiming.monitor.eventbus.EventListener;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.extensions.incidents.IncidentInfo;
import ACCLiveTiming.monitor.extensions.incidents.events.Accident;
import ACCLiveTiming.monitor.utility.TimeUtils;
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
public class GoogleSheetsAPIExtension extends AccClientExtension
        implements EventListener {

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

    public GoogleSheetsAPIExtension() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionChanged) {
            currentSessionId = ((SessionChanged) e).getSessionId();
        } else if (e instanceof Accident) {
            IncidentInfo info = ((Accident) e).getInfo();
            String sessionTime = TimeUtils.asDuration(info.getEarliestTime());
            String carNumbers = info.getCars().stream()
                    .map(car -> String.valueOf(car.getCarNumber()))
                    .collect(Collectors.joining(", "));
            String targetSheet = getTargetSheet(info.getSessionID());
            queue.add(new SendIncidentEvent(sessionTime, carNumbers, targetSheet));
            LOG.info("accident received: " + carNumbers);
        }
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
                //sendGreenFlag((GreenFlagEvent) o);
            }
        }
    }

    private void sendIncident(SendIncidentEvent event) {

        String sheet = event.targetSheet;
        String range = sheet + "B1:C500";

        List<List<Object>> values;
        try {
            values = getCells(range);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error getting spreadsheet values", e);
            return;
        }
        int emptyLine = values.size() + 1;

        range = sheet + "B" + emptyLine + ":C" + emptyLine;

        List<List<Object>> line = new LinkedList<>();
        line.add(Arrays.asList(event.sessionTime, event.carsInvolved));
        try {
            updateCells(range, line);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error sending spreadsheet values", e);
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

    private class SendIncidentEvent {

        public String sessionTime;
        public String carsInvolved;
        public String targetSheet;

        public SendIncidentEvent(String sessionTime, String carsInvolved, String targetSheet) {
            this.sessionTime = sessionTime;
            this.carsInvolved = carsInvolved;
            this.targetSheet = targetSheet;
        }
    }

    private class QuitEvent {
    }

    private class GreenFlagEvent {

        public int time;
        public SessionId sessionId;

        public GreenFlagEvent(int time, SessionId sessionId) {
            this.time = time;
            this.sessionId = sessionId;
        }
    }
}
