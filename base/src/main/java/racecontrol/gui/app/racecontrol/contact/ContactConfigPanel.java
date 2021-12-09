/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.contact;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_ADVANCED_ENABLED;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_ENABLED;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_HINT_INVALID;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_HINT_LAPCOUNT;
import static racecontrol.persistance.PersistantConfigKeys.CONTACT_CONFIG_HINT_SPIN;

/**
 *
 * @author Leonard
 */
public class ContactConfigPanel
        extends LPContainer {

    private final LPLabel enableLabel = new LPLabel("Enable collision detection");
    protected final LPCheckBox enableCheckBox = new LPCheckBox();

    private final LPLabel enableAdvancedLabel = new LPLabel("Enable advanced contact detection");
    protected final LPCheckBox enableAdvancedCheckBox = new LPCheckBox();

    private final LPLabel hintsHeadingLabel = new LPLabel("Hints to add to an incident in the spreadsheet:");

    private final LPLabel lapNumberLabel = new LPLabel("Lap number.");
    protected final LPCheckBox lapNumberCheckBox = new LPCheckBox();

    private final LPLabel spinLabel = new LPLabel("Car has spun after contact.");
    protected final LPCheckBox spinCheckBox = new LPCheckBox();

    private final LPLabel invalidLabel = new LPLabel("Cars current lap is invalid. Not during race.");
    protected final LPCheckBox invalidBox = new LPCheckBox();

    public ContactConfigPanel() {
        setName("Contact detection configuration");
        setSize(500, LINE_HEIGHT * 7);

        addComponent(enableCheckBox);
        enableCheckBox.setPosition(20, 10);
        enableCheckBox.setSelected(PersistantConfig.get(CONTACT_CONFIG_ENABLED));
        addComponent(enableLabel);
        enableLabel.setPosition(50, 0);

        addComponent(enableAdvancedCheckBox);
        enableAdvancedCheckBox.setPosition(20, LINE_HEIGHT + 10);
        enableAdvancedCheckBox.setSelected(PersistantConfig.get(CONTACT_CONFIG_ADVANCED_ENABLED));
        addComponent(enableAdvancedLabel);
        enableAdvancedLabel.setPosition(50, LINE_HEIGHT);

        addComponent(hintsHeadingLabel);
        hintsHeadingLabel.setPosition(20, LINE_HEIGHT * 3);

        addComponent(lapNumberCheckBox);
        lapNumberCheckBox.setPosition(20, LINE_HEIGHT * 4 + 10);
        lapNumberCheckBox.setSelected(PersistantConfig.get(CONTACT_CONFIG_HINT_LAPCOUNT));
        addComponent(lapNumberLabel);
        lapNumberLabel.setPosition(50, LINE_HEIGHT * 4);

        addComponent(spinCheckBox);
        spinCheckBox.setPosition(20, LINE_HEIGHT * 5 + 10);
        spinCheckBox.setSelected(PersistantConfig.get(CONTACT_CONFIG_HINT_SPIN));
        addComponent(spinLabel);
        spinLabel.setPosition(50, LINE_HEIGHT * 5);

        addComponent(invalidBox);
        invalidBox.setPosition(20, LINE_HEIGHT * 6 + 10);
        invalidBox.setSelected(PersistantConfig.get(CONTACT_CONFIG_HINT_INVALID));
        addComponent(invalidLabel);
        invalidLabel.setPosition(50, LINE_HEIGHT * 6);

        updateComponents();
    }

    @Override
    public void draw(PApplet applet) {
        applet.noStroke();
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        updateComponents();
    }

    protected void updateComponents() {
        boolean state = enableCheckBox.isSelected();
        enableAdvancedLabel.setEnabled(state);
        enableAdvancedCheckBox.setEnabled(state);
        hintsHeadingLabel.setEnabled(state);
        lapNumberLabel.setEnabled(state);
        lapNumberCheckBox.setEnabled(state);
        spinLabel.setEnabled(state);
        spinCheckBox.setEnabled(state);
        invalidLabel.setEnabled(state);
        invalidBox.setEnabled(state);

    }

}
