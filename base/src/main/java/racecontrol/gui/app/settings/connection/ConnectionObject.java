/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings.connection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Leonard
 */
public class ConnectionObject {

    @JsonProperty(value = "updListenerPort")
    public int port;

    @JsonProperty(value = "connectionPassword")
    public String connectionPassword;

    @JsonProperty(value = "commandPassword")
    public String commandPassword;
}
