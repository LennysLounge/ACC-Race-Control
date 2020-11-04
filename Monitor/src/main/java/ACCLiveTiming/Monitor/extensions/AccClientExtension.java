/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions;

import ACCLiveTiming.monitor.client.BasicAccBroadcastingClient;
import ACCLiveTiming.monitor.client.SessionId;
import ACCLiveTiming.monitor.networking.data.BroadcastingEvent;
import ACCLiveTiming.monitor.networking.data.CarInfo;
import ACCLiveTiming.monitor.networking.data.RealtimeInfo;
import ACCLiveTiming.monitor.networking.data.SessionInfo;
import ACCLiveTiming.monitor.networking.data.TrackInfo;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class AccClientExtension {
    /**
     * Base client for this extension.
     */
    protected BasicAccBroadcastingClient client;
    /**
     * The panel that is attached to this extension.
     * If null then no panel is attached.
     */
    protected LPContainer panel = null;
    /**
     * Returns true if there is an extension Panel for this extension.
     * @return If there is an extension.
     */
    public boolean hasPanel(){
        return (panel != null);
    }
    /**
     * Returns the attached panel for this extension.
     * @return The attached panel.
     */
    public LPContainer getPanel(){
        return panel;
    }
    
    public void setClient(BasicAccBroadcastingClient client){
        this.client = client;
    }
      
}
