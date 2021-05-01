/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.googlesheetsapi;

import base.screen.visualisation.gui.LPContainer;
import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

            //Load client secrets
            final String CREDENTIAL_PATH = "Google Sheets API Key/credentials.json";
            InputStream in;
            try {
                in = new FileInputStream(CREDENTIAL_PATH);
            } catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE, "Cannot load credentials file: " + CREDENTIAL_PATH, e);
                JOptionPane.showMessageDialog(null, "There was an error loading the Google API credentials."
                        + "\nThe file could not be found.",
                        "Error loading API credentials", ERROR_MESSAGE);
                return;
            }

            extension = new GoogleSheetsAPIExtension(
                    new GoogleSheetsService(configurationPanel.getSpreadSheetLink(), in));
            extension.setReplayOffsetCell(configurationPanel.getReplayOffsetCell());
            extension.setFindEmptyRowRange(configurationPanel.getFindEmptyRowRange());
            extension.setSessionColumn(configurationPanel.getSessionColumn());
            extension.setCarInfoColumn(configurationPanel.getCarColumn());
            extension.start();
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
