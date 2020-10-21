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
import ACCLiveTiming.extensions.spreadsheetcontroll.SpreadSheetControlExtension;
import ACCLiveTiming.utility.SpreadSheetService;
import ACCLiveTiming.visualisation.Visualisation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import processing.core.PApplet;

/**
 *
 * @author Leonard
 */
public class Main {

    private static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        //set logging file.
        LogManager logManager = LogManager.getLogManager();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String logPath = System.getProperty("user.dir") + "/log/" + dateFormat.format(new Date()) + ".log";
            Properties prop = new Properties();
            prop.load(Main.class.getResourceAsStream("/logging.properties"));
            prop.put("java.util.logging.FileHandler.pattern", logPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            prop.store(out, "");
            logManager.readConfiguration(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An error happened while setting up the logger.", e);
        }
        
        Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler());

        startApp();
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
            try {
                SpreadSheetService.start(dialog.getSpreadsheetURL());
            } catch (IllegalArgumentException e) {
                LOG.log(Level.WARNING, "URL is not a valid SpreadSheet url", e);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Error enabling the Spreadsheet API", e);
            }
        }

        BasicAccBroadcastingClient client;
        try {
            client = new BasicAccBroadcastingClient(dialog.getDisplayName(),
                    dialog.getConnectionPassword(),
                    dialog.getCommandPassword(),
                    dialog.getUpdateInterval(),
                    dialog.getHostAddress(),
                    dialog.getPort());
        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Error while creating the broadcasting client.", e);
            return;
        }

        client.registerExtension(new LiveTimingExtension());
        client.registerExtension(new IncidentExtension());
        client.registerExtension(new LapTimeExtension(dialog.isLapTimeLoggingEnabled()));
        client.registerExtension(new LoggingExtension());
        if (dialog.isSheetsAPIEnabled()) {
            client.registerExtension(new SpreadSheetControlExtension());
        }

        client.sendRegisterRequest();

        Visualisation v = new Visualisation(client);
        String[] a = {"MAIN"};
        PApplet.runSketch(a, v);
    }

    public static class UncoughtExceptionHandler
            implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.SEVERE, "Uncought exception:", e);
        }

    }

}
