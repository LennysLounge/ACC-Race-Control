/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.persistance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
     * Map holds the configurations.
     */
    private static Map<Key<?>, Object> configuration = new HashMap<>();

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
            configuration = (Map<Key<?>, Object>) objIn.readObject();
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            LOG.info("Persistant conig not found, creating new.");
            saveConfig();
        }
        initialised = true;
    }

    private static void saveConfig() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(configuration);
            oos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PersistantConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PersistantConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add a new property
     *
     * @param <T> Class of the value.
     * @param key The key place the value under.
     * @param value The value to save.
     */
    public static <T> void put(Key<T> key, T value) {
        configuration.put(key, value);
        saveConfig();
    }

    /**
     * Returns the property for the key.
     *
     * @param <T> Type of the value.
     * @param key The key to look for.
     * @return The value of type T.
     */
    public static <T> T get(Key<T> key) {
        if (!configuration.containsKey(key)) {
            return key.defaultValue;
        }
        return key.type.cast(configuration.get(key));
    }

    /**
     * Key to use to identify configurations.
     *
     * @param <T> Type of the object this key referes to.
     */
    public static class Key<T>
            implements Serializable {

        /**
         * Type of the object this key referes to.
         */
        final Class<T> type;
        /**
         * Default value for this key if the property does not exist.
         */
        final T defaultValue;
        /**
         * Identifier for this key.
         */
        final String identifier;

        public Key(Class<T> type, T defaultValue, String identifier) {
            this.type = type;
            this.defaultValue = defaultValue;
            this.identifier = identifier;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.type);
            hash = 71 * hash + Objects.hashCode(this.defaultValue);
            hash = 71 * hash + Objects.hashCode(this.identifier);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key<?> other = (Key<?>) obj;
            if (!Objects.equals(this.identifier, other.identifier)) {
                return false;
            }
            if (!Objects.equals(this.type, other.type)) {
                return false;
            }
            if (!Objects.equals(this.defaultValue, other.defaultValue)) {
                return false;
            }
            return true;
        }

    }
}
