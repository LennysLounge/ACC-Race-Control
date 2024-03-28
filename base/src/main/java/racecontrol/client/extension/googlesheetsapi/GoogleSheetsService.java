/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsService {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsService.class.getName());
    /**
     * Sheet service for the connection.
     */
    private static Sheets sheetService;
    /**
     * Id of the connected spreadsheet.
     */
    private String spreadSheetId;

    public GoogleSheetsService(String spreadSheetURL,
            String credentialsPath)
            throws IllegalArgumentException, RuntimeException, FileNotFoundException {
        requireNonNull(spreadSheetURL, "spreadSheetURL");

        //set spreadsheet id
        setSpreadSheetID(spreadSheetURL);

        //Load client secrets
        InputStream in;
        if (credentialsPath.isEmpty()) {
            in = GoogleSheetsService.class.getResourceAsStream("/googleapi/credentials.json");
        } else {
            final String CREDENTIAL_PATH = credentialsPath;
            in = new FileInputStream(CREDENTIAL_PATH);
        }
        try {
            sheetService = createSheetsService(in);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Error while creating sheet service", e);
        }
    }

    private void setSpreadSheetID(String spreadSheetURL)
            throws IllegalArgumentException {
        //find spreadsheet id with regex
        Pattern p = Pattern.compile(".*/spreadsheets/d/([a-zA-Z0-9-_]+).*");
        Matcher m = p.matcher(spreadSheetURL);
        if (m.matches()) {
            spreadSheetId = m.group(1);
        } else {
            throw new IllegalArgumentException("Url is not a valid google sheets URL.");
        }
    }

    private Sheets createSheetsService(InputStream credentialsFile) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(credentialsFile));
        List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        String TOKENS_DIRECTORY_PATH = "tokens";

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        int port = 8888;
        int tries = 10;
        Credential credentials = null;
        while (credentials == null) {
            try {
                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(port).build();
                credentials = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            } catch (IOException e) {
                tries -= 1;
                port += 1;
                if (tries == 0) {
                    LOG.log(Level.SEVERE, "Port blocked and no more tries left");
                    throw e;
                }
                LOG.log(Level.INFO, "Port blocked, retry with new port :" + port);
            }
        }

        return new Sheets.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName("ACCRaceControl/1.0")
                .build();
    }

    public void updateCells(String range, List<List<Object>> values) throws IOException {
        String valueInputOption = "RAW";
        ValueRange requestBody = new ValueRange();
        requestBody.setRange(range);
        requestBody.setValues(values);
        requestBody.setMajorDimension("ROWS");

        Sheets.Spreadsheets.Values.Update request
                = sheetService.spreadsheets().values().update(spreadSheetId, range, requestBody);
        request.setValueInputOption(valueInputOption);

        UpdateValuesResponse response = request.execute();
    }

    public List<List<Object>> getCells(String range) throws IOException {
        Sheets.Spreadsheets.Values.Get request
                = sheetService.spreadsheets().values().get(spreadSheetId, range);
        request.setValueRenderOption("FORMATTED_VALUE");
        request.setDateTimeRenderOption("FORMATTED_STRING");
        request.setMajorDimension("ROWS");

        ValueRange response = request.execute();
        if (response.getValues() == null) {
            return new ArrayList<>();
        }
        return response.getValues();
    }

    public Spreadsheet getSpreadsheet() throws IOException {
        Sheets.Spreadsheets.Get request = sheetService.spreadsheets().get(spreadSheetId);

        return request.execute();
    }

}
