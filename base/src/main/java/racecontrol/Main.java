/*
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol;

import racecontrol.utility.Version;
import racecontrol.gui.RaceControlApplet;
import racecontrol.persistance.PersistantConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import processing.core.PApplet;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.util.concurrent.TimeUnit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import racecontrol.persistance.PersistantConfig.Key;

/**
 *
 * @author Leonard
 */
public class Main {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler());
        setupLogging();
        LOG.info("Version: " + Version.VERSION);
        PersistantConfig.init();

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

        //start visualisation.
        String[] a = {"MAIN"};
        PApplet.runSketch(a, new RaceControlApplet());
    }

    private static void setupSplash() {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();
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

    public static class UncoughtExceptionHandler
            implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.SEVERE, "Uncought exception:", e);
        }

    }

}
