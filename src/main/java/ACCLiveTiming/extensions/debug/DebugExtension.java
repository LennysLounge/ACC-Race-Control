/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.debug;

import ACCLiveTiming.client.AccClientExtension;
import ACCLiveTiming.networking.data.AccBroadcastingData;

/**
 *
 * @author Leonard
 */
public class DebugExtension extends AccClientExtension {

    public DebugExtension() {
        this.panel = new DebugPanel(this);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }
}
