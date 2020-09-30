/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

/**
 *
 * @author Leonard
 */
public class LPTable extends LPComponent {
    
    @Override
    public void draw(){
        applet.fill(30);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());
        
        applet.strokeWeight(1);
        applet.stroke(255);
        applet.noFill();
        applet.rect(5,5,getWidth()-10, getHeight()-10);
    }
    
}
