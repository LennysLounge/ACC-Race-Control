/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.extensions.incidents.IncidentExtension;
import ACCLiveTiming.extensions.laptimes.LapTimeExtension;
import ACCLiveTiming.extensions.livetiming.LiveTimingExtension;
import ACCLiveTiming.extensions.logging.LoggingExtension;
import ACCLiveTiming.utility.SpreadSheetService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import processing.core.PApplet;

/**
 *
 * @author Leonard
 */
public class Main {

    private static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        LogManager logManager = LogManager.getLogManager();
        try {

            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String logPath = System.getProperty("user.dir") + "/log/" + dateFormat.format(now) + ".log";

            Properties prop = new Properties();
            prop.load(Main.class.getResourceAsStream("/logging.properties"));
            prop.put("java.util.logging.FileHandler.pattern", logPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            prop.store(out, "");

            logManager.readConfiguration(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An error happened while setting up the logger.", e);
        }

        try {
            startApp();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An exception has occured.", e);
        }
        
        


    }

    public static void startApp() {
        LOG.info("Starting");
        ConnectionDialog dialog = new ConnectionDialog();
        dialog.setVisible(true);

        if (!dialog.exitWithConnect()) {
            return;
        }

        //eable spreadSheet service.
        if (dialog.isSheetsAPIEnabled()) {
            SpreadSheetService.setEnable(dialog.isSheetsAPIEnabled());
            SpreadSheetService.setSpreadsheetURL(dialog.getSpreadsheetURL());

            if (!SpreadSheetService.isEnabled()) {
                JOptionPane.showMessageDialog(null, "Error enabling the Spreadsheet API");
            }
        }

        BasicAccBroadcastingClient client;
        try {
            client = new BasicAccBroadcastingClient();
        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Error while creating the visualization.", e);
            return;
        }

        client.setCredentials(dialog.getDisplayName(),
                dialog.getConnectionPassword(),
                dialog.getCommandPassword());
        client.setUpdateInterval(dialog.getUpdateInterval());
        client.connect(dialog.getHostAddress(), dialog.getPort());
        
        client.registerExtension(new LiveTimingExtension());
        client.registerExtension(new IncidentExtension());
        client.registerExtension(new LapTimeExtension());
        client.registerExtension(new LoggingExtension());

        Vis v = new Vis(client);

        String[] a = {"MAIN"};
        PApplet.runSketch(a, v);
        
    }

}
