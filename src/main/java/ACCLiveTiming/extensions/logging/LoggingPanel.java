/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.logging;

import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.LPTable;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class LoggingPanel extends LPContainer {

    private LoggingExtension extension;
    /**
     * The table that display the messages.
     */
    private LPTable table = new LPTable<LogMessage>();

    public LoggingPanel(LoggingExtension extension) {
        this.extension = extension;
        setName("LOGGING");
        
        table = new LPTable<>();
        table.addColumn("message", 100, true, LEFT, LogMessage.getMessage);
        table.drawBottomRow(true);
        addComponent(table);

    }
    
        @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

    @Override
    public void draw() {
        table.setEntries(extension.getMessages());
    }
}
