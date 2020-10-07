/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.extensions.AccClientExtension;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Leonard
 */
public class LoggingExtension extends AccClientExtension {

    public LoggingExtension() {
        this.panel = new LoggingPanel(this);
    }

    public List<LogMessage> getMessages() {
        return client.getMessages().stream()
                .map(s -> new LogMessage(s))
                .collect(Collectors.toList());
    }
}
