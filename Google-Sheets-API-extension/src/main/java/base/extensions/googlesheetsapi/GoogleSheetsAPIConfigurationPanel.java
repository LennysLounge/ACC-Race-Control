/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.googlesheetsapi;

import static base.screen.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import static base.screen.visualisation.LookAndFeel.TEXT_SIZE;
import base.screen.visualisation.gui.LPCheckBox;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPLabel;
import base.screen.visualisation.gui.LPTextField;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationPanel
        extends LPContainer {

    private final LPLabel headingLabel = new LPLabel("Google Spreadsheet API");
    private final LPCheckBox enabledCheckBox = new LPCheckBox();
    private final LPLabel enabledLabel = new LPLabel("Enable");
    private final LPLabel spreadSheetLinkLabel = new LPLabel("Spreadsheet link:");
    private final LPTextField spreadSheetLinkTextField = new LPTextField();

    public GoogleSheetsAPIConfigurationPanel() {
        setName("Google API");

        headingLabel.setPosition(20,0);
        addComponent(headingLabel);

        enabledCheckBox.setPosition(20, LINE_HEIGHT + (LINE_HEIGHT - TEXT_SIZE) / 2f);
        addComponent(enabledCheckBox);
        enabledLabel.setPosition(60, LINE_HEIGHT);
        addComponent(enabledLabel);

        spreadSheetLinkLabel.setPosition(20, LINE_HEIGHT*2);
        addComponent(spreadSheetLinkLabel);
        spreadSheetLinkTextField.setPosition(20, LINE_HEIGHT * 3);
        addComponent(spreadSheetLinkTextField);

    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(int w, int h) {
        spreadSheetLinkTextField.setSize(w - 40, LINE_HEIGHT);
    }

    public boolean isExtensionEnabled() {
        return enabledCheckBox.isSelected();
    }

    public String getSpreadSheetLink() {
        return spreadSheetLinkTextField.getValue();
    }
}
