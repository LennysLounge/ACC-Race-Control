/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.googlesheetsapi;

import racecontrol.visualisation.gui.LPContainer;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfig.CREDENTIALS_FILE_PATH;
import racecontrol.client.extension.AccClientExtension;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.extension.AccBroadcastingClientExtensionModule;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtensionModule
        implements AccBroadcastingClientExtensionModule {

    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIExtensionModule.class.getName());

    private final GoogleSheetsAPIConfigurationPanel configurationPanel;

    public GoogleSheetsAPIExtensionModule() {
        configurationPanel = new GoogleSheetsAPIConfigurationPanel();
    }

    @Override
    public boolean isEnabled() {
        return configurationPanel.isExtensionEnabled();
    }

    @Override
    public AccClientExtension createExtension(AccBroadcastingClient client) {
        GoogleSheetsAPIExtension extension = null;
        //save the credentals file path
        PersistantConfig.setConfig(CREDENTIALS_FILE_PATH, configurationPanel.getCredentialsPath());

        //create extension.
        try {
            GoogleSheetsAPIExtension e = new GoogleSheetsAPIExtension(
                    client,
                    new GoogleSheetsService(configurationPanel.getSpreadSheetLink(),
                            configurationPanel.getCredentialsPath()
                    ));
            e.setReplayOffsetCell(configurationPanel.getReplayOffsetCell());
            e.setFindEmptyRowRange(configurationPanel.getFindEmptyRowRange());
            e.setSessionColumn(configurationPanel.getSessionColumn());
            e.setCarInfoColumn(configurationPanel.getCarColumn());
            e.start();
            extension = e;
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "The spreadsheet URL is not valid.", ex);
            JOptionPane.showMessageDialog(null, "The given spreadsheet link is not valid."
                    + "\nMake sure you copy the whole URL.",
                    "Error extracting spreadsheet Id", ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, "Error starting the Google Sheets service.", ex.getCause());
            JOptionPane.showMessageDialog(null, "There was an error starting the Google API service.",
                    "Error starting API Service", ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Cannot load credentials file: ", ex);
            JOptionPane.showMessageDialog(null, "There was an error loading the Google API credentials."
                    + "\nThe file could not be found.",
                    "Error loading API credentials", ERROR_MESSAGE);
        }
        return extension;
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return configurationPanel;
    }

    @Override
    public Class getExtensionClass() {
        return GoogleSheetsAPIExtension.class;
    }

}
