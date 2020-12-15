/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor;

import acclivetiming.Monitor.visualisation.Visualisation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import processing.core.PApplet;
import acclivetiming.ACCLiveTimingExtensionModule;
import acclivetiming.Monitor.networking.PrimitivAccBroadcastingClient;
import acclivetiming.Monitor.visualisation.gui.LPContainer;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class Main {

    private static Logger LOG = Logger.getLogger(Main.class.getName());
    private static List<ACCLiveTimingExtensionModule> modules = new LinkedList<>();
    private static ConnectionDialog dialog = new ConnectionDialog();

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler());
        setupLogging();

        loadModules();
        showConfigurationDialog();
        if (dialog.exitWithConnect()) {
            startApp();
        }
        
        LOG.info("Done");
    }

    private static void setupLogging() {
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
    }

    private static void loadModules() {
        ServiceLoader.load(ACCLiveTimingExtensionModule.class).forEach(module -> {
            LOG.info("Loading extension " + module.getName());
            modules.add(module);
        });
    }

    private static void showConfigurationDialog() {
        for (ACCLiveTimingExtensionModule module : modules) {
            JPanel configurationPanel = module.getExtensionConfigurationPanel();
            if (configurationPanel != null) {
                dialog.addTabPanel(configurationPanel);
            }
        }
        dialog.setVisible(true);
    }

    public static void startApp() {
        LOG.info("Starting");

        /*
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
         */
        PrimitivAccBroadcastingClient client = new PrimitivAccBroadcastingClient();
        try {
            client.connect(dialog.getDisplayName(),
                    dialog.getConnectionPassword(),
                    dialog.getCommandPassword(),
                    dialog.getUpdateInterval(),
                    dialog.getHostAddress(),
                    dialog.getPort());
        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Error while creating the broadcasting client.", e);
            return;
        }

        //Register extensions.
        /*
        for (ACCLiveTimingExtensionModule module : modules) {
            AccClientExtension extension = module.getExtension();
            if (extension != null) {
                client.registerExtension(extension);
            }
        }
        */

        /*
        client.registerExtension(new LiveTimingExtension());
        client.registerExtension(new IncidentExtension());
        client.registerExtension(new LapTimeExtension(dialog.isLapTimeLoggingEnabled()));
        client.registerExtension(new LoggingExtension());

        if (dialog.isSheetsAPIEnabled()) {
            client.registerExtension(new SpreadSheetControlExtension());
        }
         */
        client.sendRegisterRequest();

        Visualisation v = new Visualisation(client);
        String[] a = {"MAIN"};
        PApplet.runSketch(a, v);
        
        client.waitForFinish();
    }

    public static List<LPContainer> getExtensionPanels() {
        return modules.stream()
                .map(module -> module.getExtensionPanel())
                .filter(panel -> panel != null)
                .collect(Collectors.toList());
    }

    public static class UncoughtExceptionHandler
            implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.SEVERE, "Uncought exception:", e);
        }

    }

}
