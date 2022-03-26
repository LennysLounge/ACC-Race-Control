/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import processing.core.PImage;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConnectedEvent;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsDisconnetedEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_LIGHT_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class SignInWithGooglePanel
        extends LPContainer
        implements EventListener {

    private final PImage logo;
    public final LPButton signInButton = new SignInButton("sign in");

    /**
     * Wheather the app is connected or not.
     */
    private boolean connected = false;

    public SignInWithGooglePanel() {
        EventBus.register(this);

        logo = RaceControlApplet.getApplet()
                .loadResourceAsPImage("/images/ACC-RaceControl-Logo_dark.png");

        setName("Sign in with google");
        setSize(400, 400);

        signInButton.setPosition((getWidth() - signInButton.getWidth()) / 2,
                300);
        addComponent(signInButton);
    }

    @Override
    public void draw(PApplet applet) {
        applet.background(COLOR_WHITE);

        applet.smooth(8);
        applet.image(logo,
                40,
                20);

        applet.stroke(COLOR_LIGHT_GRAY);
        applet.line(50, 170, 350, 170);
        applet.noStroke();

        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_DARK_GRAY);
        applet.textAlign(CENTER);
        if (!connected) {
            applet.text("To use the Google Sheets API", 200, 230);
            applet.text("please sign in with google.", 200, 230 + TEXT_SIZE);
        } else {
            applet.text("Successfully signed in.", 200, 230);
        }
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof GoogleSheetsConnectedEvent) {
            connected = true;
            signInButton.setVisible(false);
            invalidate();
        } else if (e instanceof GoogleSheetsDisconnetedEvent) {
            connected = false;
            signInButton.setVisible(true);
            invalidate();
        }
    }

}
