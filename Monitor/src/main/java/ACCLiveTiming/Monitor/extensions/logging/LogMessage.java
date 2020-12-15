/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.extensions.logging;

import java.util.Date;

/**
 *
 * @author Leonard
 */
public class LogMessage {
    private Date timeStamp;
    private String message;

    public LogMessage(String message){
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
