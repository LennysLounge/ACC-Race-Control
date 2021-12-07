/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import racecontrol.persistance.PersistantConfig;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.LPTextField;
import java.io.File;
import javax.swing.JFileChooser;
import processing.core.PApplet;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration.CAR_INFO_COLUMN;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration.FIND_EMPTY_ROW_RANGE;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration.REPLAY_OFFSET_CELL;
import static racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration.SESSION_TIME_COLUMN;
import static racecontrol.persistance.PersistantConfigKeys.CREDENTIALS_FILE_PATH;

/**
 *
 * @author Leonard
 */
public class ConfigurationPanel
        extends LPContainer {

    private final LPLabel spreadSheetLinkLabel = new LPLabel("Spreadsheet link:");
    protected final LPTextField spreadSheetLinkTextField = new LPTextField();

    private final LPLabel credentialsFileLabel = new LPLabel("Credentials:");
    protected final LPTextField credentialsFileTextField = new LPTextField();
    private final LPButton credentalsSearchButton = new LPButton("Search");

    private final LPCheckBox useDefaultCheckBox = new LPCheckBox();
    private final LPLabel useDaufaultLabel = new LPLabel("Use defaults");

    private final LPLabel replayOffsetLabel = new LPLabel("Replay offset cell:");
    protected final LPTextField replayOffsetTextField = new LPTextField();

    private final LPLabel findRowRangeLabel = new LPLabel("Incident range:");
    protected final LPTextField findRowRangeTextField = new LPTextField();

    private final LPLabel sessionColumnLabel = new LPLabel("Session column:");
    protected final LPTextField sessionColumnTextField = new LPTextField();

    private final LPLabel carColumnLabel = new LPLabel("Incident info column:");
    protected final LPTextField carColumnTextField = new LPTextField();

    protected final LPButton connectButton = new LPButton("Connect");

    protected boolean allowInput = true;

    public ConfigurationPanel() {
        setName("Settings");

        spreadSheetLinkLabel.setPosition(20, LINE_HEIGHT * 0);
        spreadSheetLinkLabel.setSize(200, LINE_HEIGHT);
        addComponent(spreadSheetLinkLabel);
        spreadSheetLinkTextField.setPosition(20, LINE_HEIGHT * 1);
        addComponent(spreadSheetLinkTextField);

        credentialsFileLabel.setPosition(20, LINE_HEIGHT * 2);
        credentialsFileLabel.setSize(150, LINE_HEIGHT);
        addComponent(credentialsFileLabel);
        credentialsFileTextField.setPosition(20, LINE_HEIGHT * 3);
        credentialsFileTextField.setSize(200, LINE_HEIGHT);
        credentialsFileTextField.setValue(PersistantConfig.get(CREDENTIALS_FILE_PATH));
        addComponent(credentialsFileTextField);
        credentalsSearchButton.setSize(100, LINE_HEIGHT);
        credentalsSearchButton.setAction(() -> openSearchCredentialsFileDialog());
        addComponent(credentalsSearchButton);

        useDefaultCheckBox.setPosition(20, LINE_HEIGHT * 4 + (LINE_HEIGHT - TEXT_SIZE) / 2f);
        useDefaultCheckBox.setChangeAction((state) -> {
            setDefaults();
            updateComponents();
        });
        useDefaultCheckBox.setSelected(true);
        addComponent(useDefaultCheckBox);
        useDaufaultLabel.setPosition(60, LINE_HEIGHT * 4);
        addComponent(useDaufaultLabel);

        replayOffsetLabel.setPosition(40, LINE_HEIGHT * 5);
        addComponent(replayOffsetLabel);
        replayOffsetTextField.setSize(100, LINE_HEIGHT);
        replayOffsetTextField.setPosition(220, LINE_HEIGHT * 5);
        replayOffsetTextField.setValue(REPLAY_OFFSET_CELL);
        addComponent(replayOffsetTextField);

        findRowRangeLabel.setPosition(40, LINE_HEIGHT * 6);
        addComponent(findRowRangeLabel);
        findRowRangeTextField.setSize(100, LINE_HEIGHT);
        findRowRangeTextField.setPosition(180, LINE_HEIGHT * 6);
        findRowRangeTextField.setValue(FIND_EMPTY_ROW_RANGE);
        addComponent(findRowRangeTextField);

        sessionColumnLabel.setPosition(40, LINE_HEIGHT * 7);
        addComponent(sessionColumnLabel);
        sessionColumnTextField.setSize(100, LINE_HEIGHT);
        sessionColumnTextField.setPosition(200, LINE_HEIGHT * 7);
        sessionColumnTextField.setValue(SESSION_TIME_COLUMN);
        addComponent(sessionColumnTextField);

        carColumnLabel.setPosition(340, LINE_HEIGHT * 7);
        carColumnLabel.setSize(210, LINE_HEIGHT);
        addComponent(carColumnLabel);
        carColumnTextField.setSize(100, LINE_HEIGHT);
        carColumnTextField.setPosition(550, LINE_HEIGHT * 7);
        carColumnTextField.setValue(CAR_INFO_COLUMN);
        addComponent(carColumnTextField);

        connectButton.setSize(200, LINE_HEIGHT);
        connectButton.setPosition(40, LINE_HEIGHT * 9);
        addComponent(connectButton);

        setSize(660, 460);
        updateComponents();
    }
    
    @Override
    public void setEnabled(boolean state){
        super.setEnabled(state);
        updateComponents();
    }

    public void updateComponents() {

        boolean state = allowInput;
        spreadSheetLinkLabel.setEnabled(state);
        spreadSheetLinkTextField.setEnabled(state);

        credentialsFileLabel.setEnabled(state);
        credentialsFileTextField.setEnabled(state);
        credentalsSearchButton.setEnabled(state);

        useDaufaultLabel.setEnabled(state);
        useDefaultCheckBox.setEnabled(state);

        if (allowInput) {
            state = !useDefaultCheckBox.isSelected();
        }
        replayOffsetLabel.setEnabled(state);
        replayOffsetTextField.setEnabled(state);

        findRowRangeLabel.setEnabled(state);
        findRowRangeTextField.setEnabled(state);

        sessionColumnLabel.setEnabled(state);
        sessionColumnTextField.setEnabled(state);

        carColumnLabel.setEnabled(state);
        carColumnTextField.setEnabled(state);

        connectButton.setText(allowInput ? "Connect" : "Disconnect");
    }

    private void setDefaults() {
        if (useDefaultCheckBox.isSelected()) {
            replayOffsetTextField.setValue(GoogleSheetsConfiguration.REPLAY_OFFSET_CELL);
            findRowRangeTextField.setValue(GoogleSheetsConfiguration.FIND_EMPTY_ROW_RANGE);
            sessionColumnTextField.setValue(GoogleSheetsConfiguration.SESSION_TIME_COLUMN);
            carColumnTextField.setValue(GoogleSheetsConfiguration.CAR_INFO_COLUMN);
        }

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
        credentalsSearchButton.setPosition(w - 120, LINE_HEIGHT * 3);
    }

    @Override
    public void onEnabled() {
        updateComponents();
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
            if (path.startsWith(userDir)) {
                path = path.replace(userDir, "");
            }
            credentialsFileTextField.setValue(path);
        }
    }

}
