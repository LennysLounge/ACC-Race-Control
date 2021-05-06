/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.googlesheetsapi;

import base.screen.visualisation.gui.LPContainer;
import base.ACCLiveTimingExtensionFactory;
import base.persistance.PersistantConfig;
import base.screen.extensions.AccClientExtension;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtensionFactory
        implements ACCLiveTimingExtensionFactory {

    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIExtensionFactory.class.getName());

    private GoogleSheetsAPIConfigurationPanel configurationPanel;
    private GoogleSheetsAPIExtension extension;

    public GoogleSheetsAPIExtensionFactory() {

    }

    @Override
    public String getName() {
        return "Google Sheets API extension";
    }

    @Override
    public void createExtension() {
        removeExtension();
        if (configurationPanel.isExtensionEnabled()) {
            //save the credentals file path
            PersistantConfig.setCredentialsFile(configurationPanel.getCredentialsPath());

            //create extension.
            try {
                GoogleSheetsAPIExtension e = new GoogleSheetsAPIExtension(
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

        }
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        if (configurationPanel == null) {
            configurationPanel = new GoogleSheetsAPIConfigurationPanel();
        }
        return configurationPanel;
    }

    @Override
    public void removeExtension() {
        if (extension != null) {
            extension.removeExtension();
            extension = null;
        }
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

}
