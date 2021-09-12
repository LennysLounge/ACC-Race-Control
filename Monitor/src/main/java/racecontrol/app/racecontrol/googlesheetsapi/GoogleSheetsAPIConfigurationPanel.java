/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app.racecontrol.googlesheetsapi;

import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfig.CREDENTIALS_FILE_PATH;
import static racecontrol.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import static racecontrol.LookAndFeel.TEXT_SIZE;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPCheckBox;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;
import racecontrol.lpgui.gui.LPTextField;
import java.io.File;
import javax.swing.JFileChooser;
import processing.core.PApplet;
import static racecontrol.googlesheetsapi.GoogleSheetsConfiguration.CAR_INFO_COLUMN;
import static racecontrol.googlesheetsapi.GoogleSheetsConfiguration.FIND_EMPTY_ROW_RANGE;
import static racecontrol.googlesheetsapi.GoogleSheetsConfiguration.REPLAY_OFFSET_CELL;
import static racecontrol.googlesheetsapi.GoogleSheetsConfiguration.SESSION_TIME_COLUMN;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationPanel
        extends LPContainer {

    private final LPLabel headingLabel = new LPLabel("Google Spreadsheet API");
    protected final LPCheckBox enabledCheckBox = new LPCheckBox();
    private final LPLabel enabledLabel = new LPLabel("Enable");
    private final LPLabel spreadSheetLinkLabel = new LPLabel("Spreadsheet link:");
    protected final LPTextField spreadSheetLinkTextField = new LPTextField();

    private final LPLabel credentialsFileLabel = new LPLabel("Credentials:");
    protected final LPTextField credentialsFileTextField = new LPTextField();
    private final LPButton credentalsSearchButton = new LPButton("Search");

    private final LPCheckBox useDefaultCheckBox = new LPCheckBox();
    private final LPLabel useDaufaultLabel = new LPLabel("Use defaults");

    private final LPLabel replayOffsetLabel = new LPLabel("Replay offset cell:");
    protected final LPTextField replayOffsetTextField = new LPTextField();

    private final LPLabel findRowRangeLabel = new LPLabel("Find empty row in range:");
    protected final LPTextField findRowRangeTextField = new LPTextField();

    private final LPLabel sessionColumnLabel = new LPLabel("Session column:");
    protected final LPTextField sessionColumnTextField = new LPTextField();

    private final LPLabel carColumnLabel = new LPLabel("Involved cars column:");
    protected final LPTextField carColumnTextField = new LPTextField();

    private final LPLabel addLapToCarLabel = new LPLabel("Add Lap to car number:");
    private final LPCheckBox addLapToCarCheckBox = new LPCheckBox();

    public GoogleSheetsAPIConfigurationPanel() {
        setName("Google Sheets API");

        headingLabel.setPosition(20, 0);
        headingLabel.setSize(250, LINE_HEIGHT);
        addComponent(headingLabel);

        enabledCheckBox.setPosition(20, LINE_HEIGHT + (LINE_HEIGHT - TEXT_SIZE) / 2f);
        addComponent(enabledCheckBox);
        enabledLabel.setPosition(60, LINE_HEIGHT);
        addComponent(enabledLabel);

        spreadSheetLinkLabel.setPosition(20, LINE_HEIGHT * 2);
        spreadSheetLinkLabel.setSize(200, LINE_HEIGHT);
        addComponent(spreadSheetLinkLabel);
        spreadSheetLinkTextField.setPosition(20, LINE_HEIGHT * 3);
        addComponent(spreadSheetLinkTextField);

        credentialsFileLabel.setPosition(20, LINE_HEIGHT * 4);
        credentialsFileLabel.setSize(150, LINE_HEIGHT);
        addComponent(credentialsFileLabel);
        credentialsFileTextField.setPosition(20, LINE_HEIGHT * 5);
        credentialsFileTextField.setSize(200, LINE_HEIGHT);
        credentialsFileTextField.setValue(PersistantConfig.getConfig(CREDENTIALS_FILE_PATH));
        addComponent(credentialsFileTextField);
        credentalsSearchButton.setSize(100, LINE_HEIGHT);
        credentalsSearchButton.setAction(() -> openSearchCredentialsFileDialog());
        addComponent(credentalsSearchButton);

        useDefaultCheckBox.setPosition(20, LINE_HEIGHT * 6 + (LINE_HEIGHT - TEXT_SIZE) / 2f);
        useDefaultCheckBox.setChangeAction((state) -> updateComponents());
        useDefaultCheckBox.setSelected(true);
        addComponent(useDefaultCheckBox);
        useDaufaultLabel.setPosition(60, LINE_HEIGHT * 6);
        addComponent(useDaufaultLabel);

        replayOffsetLabel.setPosition(40, LINE_HEIGHT * 7);
        replayOffsetLabel.setSize(180, LINE_HEIGHT);
        addComponent(replayOffsetLabel);
        replayOffsetTextField.setSize(100, LINE_HEIGHT);
        replayOffsetTextField.setPosition(220, LINE_HEIGHT * 7);
        replayOffsetTextField.setValue(REPLAY_OFFSET_CELL);
        addComponent(replayOffsetTextField);

        findRowRangeLabel.setPosition(40, LINE_HEIGHT * 8);
        findRowRangeLabel.setSize(240, LINE_HEIGHT);
        addComponent(findRowRangeLabel);
        findRowRangeTextField.setSize(100, LINE_HEIGHT);
        findRowRangeTextField.setPosition(280, LINE_HEIGHT * 8);
        findRowRangeTextField.setValue(FIND_EMPTY_ROW_RANGE);
        addComponent(findRowRangeTextField);

        sessionColumnLabel.setPosition(40, LINE_HEIGHT * 9);
        sessionColumnLabel.setSize(160, LINE_HEIGHT);
        addComponent(sessionColumnLabel);
        sessionColumnTextField.setSize(100, LINE_HEIGHT);
        sessionColumnTextField.setPosition(200, LINE_HEIGHT * 9);
        sessionColumnTextField.setValue(SESSION_TIME_COLUMN);
        addComponent(sessionColumnTextField);

        carColumnLabel.setPosition(340, LINE_HEIGHT * 9);
        carColumnLabel.setSize(210, LINE_HEIGHT);
        addComponent(carColumnLabel);
        carColumnTextField.setSize(100, LINE_HEIGHT);
        carColumnTextField.setPosition(550, LINE_HEIGHT * 9);
        carColumnTextField.setValue(CAR_INFO_COLUMN);
        addComponent(carColumnTextField);

        addLapToCarLabel.setPosition(40, LINE_HEIGHT * 10);
        addLapToCarLabel.setSize(220, LINE_HEIGHT);
        addComponent(addLapToCarLabel);
        addLapToCarCheckBox.setPosition(260, LINE_HEIGHT * 10 + (LINE_HEIGHT - TEXT_SIZE) / 2f);
        addLapToCarCheckBox.setSelected(true);
        addComponent(addLapToCarCheckBox);
        
        setSize(660, 440);
        
        updateComponents();
    }

    public void updateComponents() {

        boolean state = enabledCheckBox.isSelected();
        
        spreadSheetLinkLabel.setEnabled(!state);
        spreadSheetLinkTextField.setEnabled(!state);

        credentialsFileLabel.setEnabled(!state);
        credentialsFileTextField.setEnabled(!state);
        credentalsSearchButton.setEnabled(!state);

        useDaufaultLabel.setEnabled(!state);
        useDefaultCheckBox.setEnabled(!state);

        if (!state) {
            state = useDefaultCheckBox.isSelected();
        }
        replayOffsetLabel.setEnabled(!state);
        replayOffsetTextField.setEnabled(!state);

        findRowRangeLabel.setEnabled(!state);
        findRowRangeTextField.setEnabled(!state);

        sessionColumnLabel.setEnabled(!state);
        sessionColumnTextField.setEnabled(!state);

        carColumnLabel.setEnabled(!state);
        carColumnTextField.setEnabled(!state);

        addLapToCarLabel.setEnabled(!state);
        addLapToCarCheckBox.setEnabled(!state);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        spreadSheetLinkTextField.setSize(w - 40, LINE_HEIGHT);
        credentialsFileTextField.setSize(w - 160, LINE_HEIGHT);
        credentalsSearchButton.setPosition(w - 120, LINE_HEIGHT * 5);
    }

    @Override
    public void onEnabled() {
        updateComponents();
    }

    public boolean isExtensionEnabled() {
        return enabledCheckBox.isSelected();
    }

    public String getSpreadSheetLink() {
        return spreadSheetLinkTextField.getValue();
    }

    public String getCredentialsPath() {
        return credentialsFileTextField.getValue();
    }

    public String getFindEmptyRowRange() {
        if (useDefaults()) {
            return FIND_EMPTY_ROW_RANGE;
        }

        return findRowRangeTextField.getValue();
    }

    public String getReplayOffsetCell() {
        if (useDefaults()) {
            return REPLAY_OFFSET_CELL;
        }
        return replayOffsetTextField.getValue();
    }

    public String getSessionColumn() {
        if (useDefaults()) {
            return SESSION_TIME_COLUMN;
        }
        return sessionColumnTextField.getValue();
    }

    public String getCarColumn() {
        if (useDefaults()) {
            return CAR_INFO_COLUMN;
        }
        return carColumnTextField.getValue();
    }

    private boolean useDefaults() {
        return useDefaultCheckBox.isSelected();
    }

    private void openSearchCredentialsFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            String userDir = System.getProperty("user.dir") + "\\";
            
            //if the selected path is within our user directory we shorten the path to a relative path based on the user dir.
            if(path.startsWith(userDir)){
                path = path.replace(userDir, "");
            }
            credentialsFileTextField.setValue(path);
        }
    }

}
