/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.persistance;

import racecontrol.client.extension.velocitymap.VelocityMapExtension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class saves the state of the programm across multiple executions.
 *
 * @author Leonard
 */
public class PersistantConfig {

    /**
     * this classes logger.
     */
    private static final Logger LOG = Logger.getLogger(PersistantConfig.class.getName());
    /**
     * File name for the serialised config.
     */
    private static final String FILE_NAME = "PersistantConfig";
    /**
     * Indicates that this class has been initialised.
     */
    private static boolean initialised = false;
    /**
     * Map that hodls the current config.
     */
    private static Map<String, String> config = new HashMap<>();
    /**
     * Connection settings.
     */
    public static String CONNECTION_IP = "connectionIp";
    public static String CONNECTION_PORT = "connectionPort";
    public static String CONNECTION_PASSWORD = "connectionPassword";
    public static String CONNECTION_INTERVAL = "connectionInterval";
    /**
     * Path to credentials file.
     */
    public static String CREDENTIALS_FILE_PATH = "credentialsFile";
    /**
     * general extension enabled.
     */
    public static String EXTENSION_LIVE_TIMING_ENABLED = "extensionLiveTimingEnabled";
    public static String EXTENSION_INCIDENTS_ENABLED = "extensionIncidentEnabled";
    public static String EXTENSION_CAMERA_CONTROL_ENABLED = "extensionCameraControlEnabled";
    public static String EXTENSION_BROADCSATING_ENABLED = "extensionBroadcastingEnabled";

    /**
     * non instantiable.
     */
    private PersistantConfig() {
    }

    /**
     * Load config.
     */
    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialised) {
            return;
        }
        //read config
        try {
            InputStream in = new FileInputStream(FILE_NAME);
            ObjectInputStream objIn = new ObjectInputStream(in);
            config = (Map<String, String>) objIn.readObject();
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            LOG.info("Persistant conig not found, creating new.");
            createDefaults();
            saveConfig();
        }
        initialised = true;
    }

    /**
     * Creats the default values for the config.
     */
    private static void createDefaults() {
        setConfig(CONNECTION_IP, "127.0.0.1");
        setConfig(CONNECTION_PORT, "9000");
        setConfig(CONNECTION_PASSWORD, "asd");
        setConfig(CONNECTION_INTERVAL, "250");
        setConfig(CREDENTIALS_FILE_PATH, "Google Sheets Api Key\\credentials.json");
        setConfig(EXTENSION_CAMERA_CONTROL_ENABLED, String.valueOf(false));
        setConfig(EXTENSION_INCIDENTS_ENABLED, String.valueOf(true));
        setConfig(EXTENSION_LIVE_TIMING_ENABLED, String.valueOf(true));
        setConfig(EXTENSION_BROADCSATING_ENABLED, String.valueOf(false));
    }

    /**
     * Sets the value for a config by its key.
     *
     * @param key The key for the config.
     * @param value The value of the config.
     */
    public static void setConfig(String key, String value) {
        config.put(key, value);
        saveConfig();
    }

    /**
     * Returns the config for a give key.
     *
     * @param key The key of the config.
     * @return The value of the config.
     */
    public static String getConfig(String key) {
        return config.get(key);
    }

    public static boolean getConfigBoolean(String key) {
        try {
            return Boolean.valueOf(getConfig(key));
        } catch (Exception e) {

        }
        return false;
    }

    private static void saveConfig() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VelocityMapExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
