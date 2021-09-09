/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol;

import processing.event.KeyEvent;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class RaceControlPanel
        extends LPContainer {
    
    private final LPTable table;
    
    private Runnable action = ()->{};
    
    public RaceControlPanel(){
        table = new LPTable();
        addComponent(table);
    }
    
    @Override
    public void onResize(float w, float h){
        table.setSize(w, h);
        table.setPosition(0, 0);
    }
    
    public LPTable getTable(){
        return table;
    }
    
    @Override
    public void onKeyPressed(KeyEvent event) {
        if(event.getKey() == 'k'){
            action.run();
        }
    }
    
    public void setKeyEvent(Runnable action){
        this.action = action;
    }

}
