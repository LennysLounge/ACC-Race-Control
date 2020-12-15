/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.client.events;

import acclivetiming.monitor.eventbus.Event;

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
