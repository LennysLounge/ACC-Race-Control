/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.persistance;

import base.screen.extensions.velocitymap.VelocityMapExtension;
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
    private static Map<String, Object> config = new HashMap<>();

    /**
     * non instantiable.
     */
    private PersistantConfig() {
    }

    /**
     * Load config.
     */
    public static void init() {
        if (initialised) {
            return;
        }
        //read config
        try {
            InputStream in = new FileInputStream(FILE_NAME);
            ObjectInputStream objIn = new ObjectInputStream(in);
            config = (Map<String, Object>) objIn.readObject();
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
        setConfig("connectionIp", "127.0.0.1");
        setConfig("connectionPort", "9000");
        setConfig("connectionPassword", "asd");
        setConfig("connectionInterval", "250");
        setConfig("credentialsFile", "nada");
    }

    /**
     * Sets the value for a config by its key.
     *
     * @param key The key for the config.
     * @param value The value of the config.
     */
    private static void setConfig(String key, Object value) {
        config.put(key, value);
        saveConfig();
    }

    /**
     * Returns the config for a give key.
     *
     * @param key The key of the config.
     * @return The value of the config.
     */
    private static Object getConfig(String key) {
        return config.get(key);
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

    public static void setConnectionIP(String connectionIp) {
        setConfig("connectionIp", connectionIp);
    }

    public static String getConnectionIP() {
        return (String) getConfig("connectionIp");
    }

    public static void setConnectionPort(String connectionPort) {
        setConfig("connectionPort", connectionPort);
    }

    public static String getConnectionIPort() {
        return (String) getConfig("connectionPort");
    }

    public static void setConnectionPassword(String connectionPassword) {
        setConfig("connectionPassword", connectionPassword);
    }

    public static String getConnectionPassword() {
        return (String) getConfig("connectionPassword");
    }

    public static void setConnectionInterval(String connectionInterval) {
        setConfig("connectionInterval", connectionInterval);
    }

    public static String getConnectionInterval() {
        return (String) getConfig("connectionInterval");
    }
    
    public static void setCredentialsFile(String credentialsFile) {
        setConfig("credentialsFile", credentialsFile);
    }

    public static String getCredentialsFile() {
        return (String) getConfig("credentialsFile");
    }

}
