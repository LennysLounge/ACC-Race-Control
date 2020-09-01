/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.client.AccClientExtension;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class LoggingExtension extends AccClientExtension {

    public LoggingExtension() {
        this.panel = new LoggingPanel(this);
    }
    
    public List<String> getMessages(){
        return client.getMessages();
    }
}
