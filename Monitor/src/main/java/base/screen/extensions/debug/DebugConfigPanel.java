/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.debug;

import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import static base.screen.visualisation.LookAndFeel.TEXT_SIZE;
import base.screen.visualisation.gui.LPCheckBox;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPLabel;

/**
 *
 * @author Leonard
 */
public class DebugConfigPanel
        extends LPContainer {

    private final LPCheckBox enabledCheckBox;
    private final LPLabel enabledLabel;

    public DebugConfigPanel() {
        setName("Debug");

        enabledCheckBox = new LPCheckBox();
        enabledCheckBox.setPosition(20, (LINE_HEIGHT - TEXT_SIZE) / 2);
        addComponent(enabledCheckBox);
        enabledLabel = new LPLabel("Enable");
        enabledLabel.setPosition(60, 0);
        addComponent(enabledLabel);

    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public boolean isExtensionEnabled() {
        return enabledCheckBox.isSelected();
    }

}
