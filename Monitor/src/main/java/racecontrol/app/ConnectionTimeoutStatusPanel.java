/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.LookAndFeel;
import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.COLOR_ORANGE;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;

/**
 * A Status panel to show that the connection timed out.
 *
 * @author Leonard
 */
public class ConnectionTimeoutStatusPanel
        extends LPContainer {

    private final LPLabel message = new LPLabel("Connection timed out, the game client stopped sending data.");
    private final LPButton dismiss = new LPButton("Dismiss");

    public ConnectionTimeoutStatusPanel() {
        dismiss.setSize(100, LINE_HEIGHT);
        addComponent(dismiss);
        dismiss.setAction(() -> {
            AppController.getInstance().removeStatusPanel(this);
        });
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_ORANGE);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(COLOR_DARK_GRAY);
        applet.textFont(LookAndFeel.fontMedium());
        applet.textAlign(LEFT, CENTER);
        applet.text("Connection timed out, the game client stopped sending data.", 10, getHeight() / 2f);
    }

    @Override
    public void onResize(float w, float h) {
        message.setPosition(20, 0);
        dismiss.setPosition(w - 120, 0);
    }
}
