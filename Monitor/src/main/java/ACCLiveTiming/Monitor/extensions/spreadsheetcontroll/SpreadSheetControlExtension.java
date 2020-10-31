/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.spreadsheetcontroll;

import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.networking.data.AccBroadcastingData;

/**
 *
 * @author Leonard
 */
public class SpreadSheetControlExtension extends AccClientExtension {

    public SpreadSheetControlExtension() {
        this.panel = new SpreadSheetControlPanel(this);
    }

    public AccBroadcastingData getModel() {
        return client.getModel();
    }
}
