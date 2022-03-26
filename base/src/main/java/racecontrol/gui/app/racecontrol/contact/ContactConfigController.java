/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.contact;

import racecontrol.client.extension.contact.ContactExtension;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.AppController;

/**
 *
 * @author Leonard
 */
public class ContactConfigController {

    /**
     * Reference to the contact extension.
     */
    private final ContactExtension contactExtension;
    /**
     * The panel this controller controls.
     */
    private final ContactConfigPanel panel;

    public ContactConfigController() {
        contactExtension = ContactExtension.getInstance();
        panel = new ContactConfigPanel();

        panel.enableCheckBox.setChangeAction((state) -> {
            contactExtension.setEnabled(state);
            panel.updateComponents();
        });

        panel.enableAdvancedCheckBox.setChangeAction((state) -> {
            contactExtension.setAdvancedEnabled(state);
        });

        panel.lapNumberCheckBox.setChangeAction((state) -> {
            contactExtension.setSendLapNumber(state);
        });

        panel.spinCheckBox.setChangeAction((state) -> {
            contactExtension.setSendSpin(state);
        });

        panel.invalidBox.setChangeAction((state) -> {
            contactExtension.setSendInvalid(state);
        });
    }

    public void openSettingsPanel() {
        RaceControlApplet.launchNewWindow(panel, false);
    }

}
