/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.client.AccClientExtension;
import ACCLiveTiming.networking.data.AccBroadcastingData;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtension extends AccClientExtension {

    private static Logger LOG = Logger.getLogger(LiveTimingExtension.class.getName());
    
    public LiveTimingExtension() {
        this.panel = new LiveTimingPanel(this);
    }
    
    public AccBroadcastingData getModel(){
        return client.getModel();
    }
}
