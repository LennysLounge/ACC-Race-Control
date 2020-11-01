/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googlesheetsapi;

import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.networking.data.AccBroadcastingData;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIExtension extends AccClientExtension {

    public GoogleSheetsAPIExtension() {
        this.panel = new GoogleSheetsAPIPanel(this);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }
}
