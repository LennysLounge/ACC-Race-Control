/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.testomato;

import ACCLiveTiming.visualisation.gui.LPContainer;
import ACCLiveTiming.visualisation.gui.NewLPTable;

/**
 *
 * @author Leonard
 */
public class TestomatoPanel extends LPContainer {
    
    private NewLPTable table;
    
    public TestomatoPanel(){
        table = new NewLPTable();
        addComponent(table);
    }

    @Override
    public void draw() {
        applet.fill(200);
        applet.rect(0, 0, getWidth(), getHeight());
    }
    
    @Override
    public void onResize(int w, int h){
        table.setSize(w*0.8f, h*0.8f);
        table.setPosition(w*0.1f, h*0.1f);
    }

}
