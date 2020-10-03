/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.utility;

import ACCLiveTiming.Main;
import ACCLiveTiming.client.SessionId;
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
import java.util.Optional;
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
public class SpreadSheetService {

    private static Logger LOG = Logger.getLogger(SpreadSheetService.class.getName());

    private static Sheets sheetService;

    private static String spreadSheetID;

    private static Thread eventLoop;

    private static LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    private static boolean running = false;
    
    private static Optional<String> targetSheet = Optional.empty();

    private SpreadSheetService() {
    }

    public static boolean isRunning() {
        return running;
    }

    public static void start(String spreadSheetURL) throws IllegalArgumentException, RuntimeException {
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

    private static void eventLoop() throws InterruptedException {
        while (running) {
            Object o = queue.take();
            if (o instanceof QuitEvent) {
                running = false;
            }
            if (o instanceof AccidentEvent) {
                sendAccident((AccidentEvent) o);
            }
            if (o instanceof GreenFlagEvent) {
                sendGreenFlag((GreenFlagEvent) o);
            }
        }
    }

    public static void sendAccident(List<Integer> carNumber, float sessionTime, SessionId id) {
        queue.add(new AccidentEvent(carNumber, sessionTime, id));
    }

    private static void sendAccident(AccidentEvent event) {
        String carNumbers = event.carNumbers.stream()
                .map(i -> String.valueOf(i))
                .collect(Collectors.joining(", "));

        LOG.info("Sending Accident: " + carNumbers + ", " + TimeUtils.asDuration(event.sessionTime));
        
        if (targetSheet.isEmpty()) {
            return;
        }
        String sheet = targetSheet.get();
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
        String sessionTime = TimeUtils.asDuration(event.sessionTime);
        String carsInvolved = event.carNumbers.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(", "));
        line.add(Arrays.asList(sessionTime, carsInvolved));
        try {
            updateCells(range, line);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error sending spreadsheet values", e);
            return;
        }
    }

    public static void sendGreenFlagOffset(int time, SessionId id) {
        queue.add(new GreenFlagEvent(time, id));
    }

    private static void sendGreenFlag(GreenFlagEvent event) {
        if (targetSheet.isEmpty()) {
            return;
        }
        String sheet = targetSheet.get();
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

        range = sheet + "D" + rowNumber + ":D" + rowNumber;
        try {
            updateCells(range, Arrays.asList(Arrays.asList(TimeUtils.asDuration(event.time))));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error setting spreadsheet value", e);
            return;
        }
    }

    public static Optional<String> getSheet(SessionId sessionId) {
        switch (sessionId.getType()) {
            case PRACTICE:
                return Optional.of("Practice!");
            case QUALIFYING:
                return Optional.of("Qualifying!");
            case RACE:
                if (sessionId.getNumber() < 2) {
                    return Optional.of("Race " + (sessionId.getNumber() + 1) + "!");
                } else {
                    return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }
    
    public static void setTargetSheetBySession(SessionId sessionId){
        targetSheet = getSheet(sessionId);
    }
    
    public static void setTargetSheet(String sheet){
        targetSheet = Optional.of(sheet);
    }

    private static void updateCells(String range, List<List<Object>> values) throws IOException {
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

    private static List<List<Object>> getCells(String range) throws IOException {
        Sheets.Spreadsheets.Values.Get request
                = sheetService.spreadsheets().values().get(spreadSheetID, range);
        request.setValueRenderOption("FORMATTED_VALUE");
        request.setDateTimeRenderOption("FORMATTED_STRING");
        request.setMajorDimension("ROWS");

        ValueRange response = request.execute();

        return response.getValues();
    }

    private static Sheets createSheetsService() throws IOException, GeneralSecurityException {
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

    private static class AccidentEvent {

        public List<Integer> carNumbers;
        public float sessionTime;
        public SessionId sessionID;

        public AccidentEvent(List<Integer> carNumbers, float sessionTime, SessionId id) {
            this.carNumbers = carNumbers;
            this.sessionTime = sessionTime;
            this.sessionID = id;
        }
    }

    private static class QuitEvent {
    }

    private static class GreenFlagEvent {

        public int time;
        public SessionId sessionId;

        public GreenFlagEvent(int time, SessionId sessionId) {
            this.time = time;
            this.sessionId = sessionId;
        }
    }
}
