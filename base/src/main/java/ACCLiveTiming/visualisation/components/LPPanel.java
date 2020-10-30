/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.components;

import ACCLiveTiming.visualisation.gui.LPContainer;


/**
 *
 * @author Leonard
 */
public class LPPanel
        extends LPContainer {
    
    private int drawCounter = 0;
    
    public int color = 50;
    
    public LPPanel(){
    }
    
    @Override
    public void draw(){
        applet.fill(color);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());
        
        applet.strokeWeight(1);
        applet.stroke(255);
        applet.noFill();
        applet.rect(5,5,getWidth()-10, getHeight()-10);
    }
    
    @Override
    public void onResize(int w, int h){
    }
   

}
