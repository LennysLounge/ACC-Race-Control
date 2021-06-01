/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.logging;

import java.util.Date;

/**
 *
 * @author Leonard
 */
public class LogMessage {

    private Date timeStamp;
    private String message;

    public LogMessage(String message) {
        this.message = message;
        this.timeStamp = new Date();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }
}
