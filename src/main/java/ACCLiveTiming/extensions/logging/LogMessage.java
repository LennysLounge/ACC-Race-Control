/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.function.Function;

/**
 *
 * @author Leonard
 */
public class LogMessage extends LPTable.Entry {

    private String message;
    public static Function<LogMessage, String> getMessage = (m) -> {
        return m.getMessage();
    };

    public LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
