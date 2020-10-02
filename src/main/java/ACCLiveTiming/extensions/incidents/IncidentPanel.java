/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.incidents;

import ACCLiveTiming.extensions.livetiming.LiveTimingEntry;
import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.LPTable;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class IncidentPanel extends LPContainer {

    private final IncidentExtension extension;
    /**
     * The table that display the incidents.
     */
    private LPTable table = new LPTable<LiveTimingEntry>();

    public IncidentPanel(IncidentExtension extension) {
        this.extension = extension;
        setName("INCIDENTS");

        LPTable<Accident> t = new LPTable<>();
        t.addColumn("Nr.", 40, false, Accident.numberRenderer);
        t.addColumn("Session Time", 250, false, LEFT, Accident.getTime);
        t.addColumn("#", 50, true, Accident.carNumberRenderer);
        t.drawBottomRow(true);
        table = t;
        addComponent(table);
    }

    @Override
    public void onResize(int w, int h) {
        table.setPosition(0, 0);
        table.setSize(w, h);
    }

    @Override
    public void draw() {
        table.setEntries(extension.getAccidents());
    }

}
