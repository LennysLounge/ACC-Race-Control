/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import processing.core.PApplet;
import processing.core.PImage;
import racecontrol.eventbus.EventBus;
import racecontrol.gui.CustomPApplet;
import racecontrol.gui.lpui.LPButton;

/**
 *
 * @author Leonard
 */
public class SignInButton
        extends LPButton{

    private PImage normal;
    private PImage hover;
    private PImage pressed;


    public SignInButton(String text) {
        super(text);
        normal = ((CustomPApplet) getApplet())
                .loadResourceAsPImage("/images/google/btn_google_signin_light_normal_web.png");
        hover = ((CustomPApplet) getApplet())
                .loadResourceAsPImage("/images/google/btn_google_signin_light_focus_web.png");
        pressed = ((CustomPApplet) getApplet())
                .loadResourceAsPImage("/images/google/btn_google_signin_light_pressed_web.png");

        setSize(0, 0);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(255);
        applet.noStroke();
        applet.rect(0, 0, getWidth(), getHeight());
        if (isMouseOver()) {
            if (!isPressed()) {
                applet.image(normal, 0, 0, getWidth(), getHeight());
            } else {
                applet.image(pressed, 0, 0, getWidth(), getHeight());
            }
        } else {
            applet.image(normal, 0, 0, getWidth(), getHeight());
        }
    }

    

    public void setSize(float w, float h) {
        super.setSize(normal.width, normal.height);
    }

}
