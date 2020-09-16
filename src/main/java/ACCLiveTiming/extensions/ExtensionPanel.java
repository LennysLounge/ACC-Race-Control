/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions;

import ACCLiveTiming.visualisation.CustomPApplet;
import ACCLiveTiming.visualisation.Visualisation;
import processing.core.PApplet;

/**
 *
 * @author Leonard
 */
public abstract class ExtensionPanel {

    protected String displayName = "none";

    protected CustomPApplet applet;

    protected int posX;

    protected int posY;
    
    protected int width;
    
    protected int height;

    public String getDisplayName() {
        return displayName;
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public abstract void drawPanel();

    public void mousePressed(int mouseButton, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseButton, int mouseX, int mouseY) {
    }

    public void mouseWheel(int count) {
    }

    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void setPApplet(CustomPApplet applet) {
        this.applet = applet;
    }

    public PApplet getApplet() {
        return applet;
    }

}
