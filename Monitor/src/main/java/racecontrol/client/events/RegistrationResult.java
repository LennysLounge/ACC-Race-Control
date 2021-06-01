/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class RegistrationResult extends Event {

    private final int connectionId;
    private final boolean success;
    private final boolean readOnly;
    private final String message;

    public RegistrationResult(int connectionId,
            boolean success,
            boolean readOnly,
            String message) {
        this.connectionId = connectionId;
        this.success = success;
        this.readOnly = readOnly;
        this.message = message;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getMessage() {
        return message;
    }

}
