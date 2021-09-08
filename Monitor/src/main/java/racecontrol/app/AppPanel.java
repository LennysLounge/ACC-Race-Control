/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPComponent;
import racecontrol.lpgui.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class AppPanel 
    extends LPContainer{
    
    /**
     * Header shows the connection status and basic information.
     */
    private final HeaderPanel header;
    /**
     * Menu to select the visible page.
     */
    private final Menu menu;
    
    private LPComponent activePage;
    
    public AppPanel(){
        header = new HeaderPanel();
        addComponent(header);
        
        menu = new Menu();
        addComponent(menu);
        
        updateComponents();
    }
    
    @Override
    public void onResize(float w, float h){
        updateComponents();
    }
    
    public final void updateComponents(){
        menu.setSize(200,getHeight());
        menu.setPosition(0, 0);
        
        float menuWidth = menu.isVisible() ? menu.getWidth() : 0;
        header.setSize(getWidth() - menuWidth, LINE_HEIGHT);
        header.setPosition(menuWidth, 0);
        
        if(activePage != null){
            activePage.setSize(getWidth()-menuWidth, getHeight()-LINE_HEIGHT);
            activePage.setPosition(menuWidth, LINE_HEIGHT);
        }
    }
    
    public Menu getMenu(){
        return menu;
    }
    
    public HeaderPanel getHeader(){
        return header;
    }
    
    public void setActivePage(LPComponent page){
        if(activePage != null){
            removeComponent(activePage);
        }
        activePage = page;
        addComponent(page);
    }
}
