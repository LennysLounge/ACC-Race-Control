/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.logging;

import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import ACCLiveTiming.monitor.visualisation.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class LoggingPanel extends LPContainer {

    private LoggingExtension extension;
    /**
     * The table that display the messages.
     */
    private LPTable table = new LPTable();

    public LoggingPanel(LoggingExtension extension) {
        this.extension = extension;
        setName("LOGGING");
        
        table.setTableModel(extension.getTableModel());
        table.setOverdrawForLastLine(true);
        addComponent(table);
    }
    
        @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }
}
