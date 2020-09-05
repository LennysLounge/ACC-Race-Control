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
    
    protected int posX;
    
    protected int posY;

    public String getDisplayName() {
        return displayName;
    }
    
    public void setSize(int width, int height){
        if(layer.width != width || layer.height != height){
            //TODO:
        }
    }
    
    public void setPosition(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }
    
    public int getPosX(){
        return posX;
    }
    public int getPosY(){
        return posY;
    }

    public abstract void drawPanel();

    public void mousePressed(int mouseButton, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseButton, int mouseX, int mouseY) {
    }
    
    public void mouseWheel(int count){
    }
    
    public void onResize(int w, int h){
    }
    
    public void setLayer(PGraphics layer){
        this.layer = layer;
    }
    
    public PGraphics getLayer(){
        return layer;
    }
    
    
}
