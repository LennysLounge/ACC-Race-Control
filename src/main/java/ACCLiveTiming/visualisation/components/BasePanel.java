/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.components;

import ACCLiveTiming.client.BasicAccBroadcastingClient;
import ACCLiveTiming.visualisation.LookAndFeel;
import ACCLiveTiming.visualisation.gui.LPComponent;
import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class BasePanel extends LPContainer{
    
    private final LPComponent header;
    private final LPComponent body;
    
    public BasePanel(BasicAccBroadcastingClient client){
        header = new HeaderPanel(client);
        addComponent(header);
        
        
        LPTabPanel tabs = new LPTabPanel();
        LPPanel l = new LPPanel();
        l.setName("T1");
        l.color = 50;
        tabs.addTab(l);
        l = new LPPanel();
        l.setName("T2");
        l.color = 100;
        tabs.addTab(l);
        l = new LPPanel();
        l.setName("T3");
        l.color = 150;
        tabs.addTab(l);
        tabs.setTabIndex(0);
        body = tabs;
        addComponent(body);
    }
    
    @Override
    public void onResize(int w, int h){
        header.setSize(w, LookAndFeel.get().LINE_HEIGHT);
        header.setPosition(0, 0);
        body.setSize(w, h-LookAndFeel.get().LINE_HEIGHT);
        body.setPosition(0, LookAndFeel.get().LINE_HEIGHT);
        invalidate();
    }
    
}
