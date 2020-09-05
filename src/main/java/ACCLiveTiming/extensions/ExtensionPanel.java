/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions;

import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
public abstract class ExtensionPanel {

    protected String displayName = "none";
    
    protected PGraphics layer;

    public String getDisplayName() {
        return displayName;
    }

    public abstract void drawPanel();

    public void mousePressed(int mouseButton, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseButton, int mouseX, int mouseY) {
    }
    
    public void mouseWheel(int count){
    }
    
    public void setLayer(PGraphics layer){
        this.layer = layer;
    }
    
    public PGraphics getLayer(){
        return layer;
    }
}
