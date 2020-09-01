/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.utility;

import ACCLiveTiming.Main;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard
 */
public class SpreadSheetService {

    private static Logger LOG = Logger.getLogger(SpreadSheetService.class.getName());

    private static SpreadSheetService instance;

    private Sheets sheetService;

    private String spreadSheetID;

    private boolean enabled = false;

    private SpreadSheetService() {
    }

    public static SpreadSheetService getInstance() {
        if (instance == null) {
            instance = new SpreadSheetService();
        }
        return instance;
    }

    public static void setSpreadsheetURL(String url) throws IllegalArgumentException {
        //find spreadsheet id with regex
        Pattern p = Pattern.compile(".*/spreadsheets/d/([a-zA-Z0-9-_]+).*");
        Matcher m = p.matcher(url);
        if (m.matches()) {
            SpreadSheetService.getInstance().spreadSheetID = m.group(1);
        } else {
            throw new IllegalArgumentException("Url is not a valid google sheets URL.");
        }
    }

    public static void setEnable(boolean state) {
        if (state) {
            try {
                SpreadSheetService.getInstance().sheetService
                        = createSheetsService();
                SpreadSheetService.getInstance().enabled = state;
            } catch (IOException | GeneralSecurityException e) {
                LOG.log(Level.SEVERE, "Error while creating sheet service", e);
            }
        }
    }

    public static boolean isEnabled() {
        return SpreadSheetService.getInstance().enabled;
    }

    public static void updateCell(String range, String value) throws IOException {
        List<List<Object>> values = new LinkedList<>();
        values.add(Arrays.asList(value));
        updateCells(range, values);
    }

    public static void updateCells(String range, List<List<Object>> values) throws IOException {
        SpreadSheetService instance = SpreadSheetService.getInstance();
        if (!instance.enabled) {
            return;
        }
        Sheets sheetService = instance.sheetService;
        String spreadSheetID = instance.spreadSheetID;

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

    public static String getCell(String range) throws IOException {
        List<List<Object>> result = getCells(range);
        return (String) (result.get(0).get(0));
    }

    public static List<List<Object>> getCells(String range) throws IOException {
        SpreadSheetService instance = SpreadSheetService.getInstance();
        Preconditions.checkArgument(instance.enabled == true,
                "Google Sheets API not enabled.");

        Sheets sheetService = instance.sheetService;
        String spreadsheetID = instance.spreadSheetID;

        Sheets.Spreadsheets.Values.Get request
                = sheetService.spreadsheets().values().get(spreadsheetID, range);
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
}
