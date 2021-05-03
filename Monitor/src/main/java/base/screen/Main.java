/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen;

import base.Version;
import base.screen.visualisation.Visualisation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import processing.core.PApplet;
import base.screen.networking.AccBroadcastingClient;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.util.concurrent.TimeUnit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Leonard
 */
public class Main {

    /**
     * This classes logger.
     */
    private static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Connection client.
     */
    private static AccBroadcastingClient client;
    /**
     * Visualisation for this program.
     */
    private static Visualisation visualisation;

    public static void main(String[] args) throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler());
        setupLogging();
        
        setupSplash();
        TimeUnit.SECONDS.sleep(2);

        //Set system look and feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            LOG.log(Level.WARNING, "Error setting system look and feel.", ex);
        }
        //create a client and start visualisation.
        client = new AccBroadcastingClient();
        visualisation = new Visualisation(client);

        String[] a = {"MAIN"};
        PApplet.runSketch(a, visualisation);
    }

    private static void setupSplash() {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();
            final String[] comps = {"foo", "bar", "baz"};
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(120, 140, 200, 40);
            g.setPaintMode();
            g.setColor(Color.WHITE);
            g.drawString("Created by Leonard Schüngel", 10, 330);
            g.drawString("Version: " + Version.VERSION, 500, 330);
            splash.update();
        }
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

    public static AccBroadcastingClient getClient() {
        return client;
    }

    public static class UncoughtExceptionHandler
            implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.SEVERE, "Uncought exception:", e);
        }

    }

}
