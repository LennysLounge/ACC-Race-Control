/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.googlesheetsapi;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.requireNonNull;
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

    public GoogleSheetsService(String spreadSheetURL, InputStream credentials)
            throws IllegalArgumentException, RuntimeException {
        requireNonNull(spreadSheetURL, "spreadSheetURL");

        setSpreadSheetID(spreadSheetURL);
        try {
            sheetService = createSheetsService(credentials);
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
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(credentialsFile));
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

        return response.getValues();
    }

}
