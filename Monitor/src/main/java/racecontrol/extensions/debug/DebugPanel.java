/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.debug;

import racecontrol.Main;
import racecontrol.extensions.replayoffset.ReplayOffsetExtension;
import racecontrol.visualisation.LookAndFeel;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import racecontrol.visualisation.gui.LPButton;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPLabel;
import racecontrol.visualisation.gui.LPTextField;

/**
 *
 * @author Leonard
 */
public class DebugPanel
        extends LPContainer {

    LPButton button = new LPButton("Find replay time");
    LPTextField textField = new LPTextField();
    LPLabel label = new LPLabel("Replay time known: false");

    boolean isReplayKnown = false;

    public DebugPanel() {
        setName("Debug");

        button.setSize(200, LookAndFeel.LINE_HEIGHT);
        button.setPosition(20, 0);
        button.setAction(() -> {
            //ReplayOffsetExtension.findSessionChange();
            /*
            Main.getClient().sendInstantReplayRequest(
                    Main.getClient().getModel().getSessionInfo().getSessionTime() / 1000,
                    10
            );
             */
        });
        addComponent(button);

        label.setPosition(20, LINE_HEIGHT);
        addComponent(label);

        textField.setSize(300, LookAndFeel.LINE_HEIGHT);
        textField.setPosition(20, LINE_HEIGHT * 2);
        textField.setValue("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        addComponent(textField);

    }

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public void setReplayTimeKnown() {
        label.setText("Replay time known: true");
        //button.setEnabled(false);
    }

}
