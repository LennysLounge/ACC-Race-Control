/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.persistance;

import racecontrol.persistance.PersistantConfig.Key;

/**
 *
 * @author Leonard
 */
public interface PersistantConfigKeys {

    /**
     * Path to credentials file.
     */
    public Key<String> CREDENTIALS_FILE_PATH = new Key<>(String.class, "Google Sheets Api Key\\credentials.json", "credentialsFile");

    /**
     * Connection settings.
     */
    public Key<String> CONNECTION_IP = new Key<>(String.class, "127.0.0.1", "connectionIp");
    public Key<String> CONNECTION_PORT = new Key<>(String.class, "9000", "connectionPort");
    public Key<String> CONNECTION_PASSWORD = new Key<>(String.class, "asd", "connectionPassword");
    public Key<String> CONNECTION_COMMAND_PW = new Key<>(String.class, "", "commandPassword");
    public Key<String> CONNECTION_INTERVAL = new Key<>(String.class, "100", "connectionInterval");
    
    public Key<Boolean> BROADCASTING_CONTROLS_COLLAPSED = new Key<>(Boolean.class, false, "broadcastingControlsCollapsed");
    public Key<Boolean> MENU_COLLAPSED = new Key<>(Boolean.class, false, "menuCollapsed");
    
    /**
     * Contact config panel
     */
    public Key<Boolean> CONTACT_CONFIG_ENABLED = new Key<>(Boolean.class, true, "contactConfigEnabled");
    public Key<Boolean> CONTACT_CONFIG_ADVANCED_ENABLED = new Key<>(Boolean.class, true, "contactConfigAdvancedEnabled");
    public Key<Boolean> CONTACT_CONFIG_HINT_LAPCOUNT = new Key<>(Boolean.class, true, "contactConfigHintLapCount");
    public Key<Boolean> CONTACT_CONFIG_HINT_SPIN = new Key<>(Boolean.class, true, "contactConfigHintSpin");
    public Key<Boolean> CONTACT_CONFIG_HINT_INVALID = new Key<>(Boolean.class, true, "contactConfigHintInvalid");
    
    
}
