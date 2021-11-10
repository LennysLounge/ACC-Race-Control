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
    public Key<String> CONNECTION_INTERVAL = new Key<>(String.class, "100", "connectionInterval");
    
    public Key<Boolean> BROADCASTING_CONTROLS_COLLAPSED = new Key<>(Boolean.class, false, "broadcastingControlsCollapsed");
    public Key<Boolean> MENU_COLLAPSED = new Key<>(Boolean.class, false, "menuCollapsed");
    
}
