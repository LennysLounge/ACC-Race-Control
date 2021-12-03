/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.gui.app.statuspanel.StatusPanelManager;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.COLOR_ORANGE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;

/**
 * A Status panel to show that the connection timed out.
 *
 * @author Leonard
 */
public class ConnectionReadOnlyStatusPanel
        extends LPContainer {

    private final LPButton dismiss = new LPButton("Dismiss");

    public ConnectionReadOnlyStatusPanel() {
        dismiss.setSize(100, LINE_HEIGHT);
        addComponent(dismiss);
        dismiss.setAction(() -> {
            StatusPanelManager.getInstance().removeStatusPanel(this);
        });
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_ORANGE);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(COLOR_DARK_GRAY);
        applet.textFont(LookAndFeel.fontMedium());
        applet.textAlign(LEFT, CENTER);
        applet.text("Connection is in read only mode.", 10, getHeight() / 2f);
    }

    @Override
    public void onResize(float w, float h) {
        dismiss.setPosition(w - 120, 0);
    }
}
