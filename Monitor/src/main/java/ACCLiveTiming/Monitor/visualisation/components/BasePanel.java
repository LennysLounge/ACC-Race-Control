/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.visualisation.components;

import acclivetiming.monitor.Main;
import acclivetiming.monitor.networking.PrimitivAccBroadcastingClient;
import acclivetiming.monitor.visualisation.LookAndFeel;
import acclivetiming.monitor.visualisation.gui.LPComponent;
import acclivetiming.monitor.visualisation.gui.LPContainer;
import acclivetiming.monitor.visualisation.gui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class BasePanel extends LPContainer {

    private final LPComponent header;
    private final LPComponent body;

    public BasePanel(PrimitivAccBroadcastingClient client) {
        header = new HeaderPanel(client);
        addComponent(header);

        LPTabPanel tabs = new LPTabPanel();
        for (LPContainer c : Main.getExtensionPanels()) {
            tabs.addTab(c);
        }
        tabs.setTabIndex(0);
        body = tabs;
        addComponent(body);
    }

    @Override
    public void onResize(int w, int h) {
        int headerSize = LookAndFeel.LINE_HEIGHT;
        header.setSize(w, headerSize);
        header.setPosition(0, 0);

        body.setSize(w, h - headerSize);
        body.setPosition(0, headerSize);
        invalidate();
    }

}
