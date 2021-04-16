/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions;

import static base.screen.visualisation.LookAndFeel.LINE_HEIGHT;
import static base.screen.visualisation.LookAndFeel.TEXT_SIZE;
import base.screen.visualisation.gui.LPCheckBox;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPLabel;

/**
 *
 * @author Leonard
 */
public class GeneralExtentionConfigPanel
        extends LPContainer {

    private final LPLabel headingLabel = new LPLabel("Settings for general features.");

    private final LPCheckBox liveTimingCheckBox = new LPCheckBox();
    private final LPLabel liveTimingLabel = new LPLabel("Enable live timing screen");
    
    private final LPCheckBox incidentLogCheckBox = new LPCheckBox();
    private final LPLabel incidentLogLabel = new LPLabel("Enable incidents screen");
    
    private final LPCheckBox loggingCheckBox = new LPCheckBox();
    private final LPLabel loggingLabel = new LPLabel("Enable logging screen");
    
    private final LPCheckBox trackMapCheckBox = new LPCheckBox();
    private final LPLabel trackMapLabel = new LPLabel("Enable track map");

    /**
     * Singleton instance of this class.
     */
    private static GeneralExtentionConfigPanel instance = null;

    /**
     * privat constructor. To get an instance of this class use getInstance.
     */
    private GeneralExtentionConfigPanel() {
        setName("General");
        headingLabel.setPosition(20, 0);
        headingLabel.setSize(300, LINE_HEIGHT);
        addComponent(headingLabel);

        liveTimingCheckBox.setPosition(20, LINE_HEIGHT + (LINE_HEIGHT - TEXT_SIZE) / 2);
        liveTimingCheckBox.setSelected(true);
        addComponent(liveTimingCheckBox);
        liveTimingLabel.setPosition(60, LINE_HEIGHT);
        liveTimingLabel.setSize(300,LINE_HEIGHT);
        addComponent(liveTimingLabel);
        
        incidentLogCheckBox.setPosition(20, LINE_HEIGHT*2 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        incidentLogCheckBox.setSelected(true);
        addComponent(incidentLogCheckBox);
        incidentLogLabel.setPosition(60, LINE_HEIGHT*2);
        incidentLogLabel.setSize(300,LINE_HEIGHT);
        addComponent(incidentLogLabel);
        
        loggingCheckBox.setPosition(20, LINE_HEIGHT*3 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        loggingCheckBox.setSelected(true);
        addComponent(loggingCheckBox);
        loggingLabel.setPosition(60, LINE_HEIGHT*3);
        loggingLabel.setSize(300,LINE_HEIGHT);
        addComponent(loggingLabel);
        
        trackMapCheckBox.setPosition(20, LINE_HEIGHT*4 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        trackMapCheckBox.setSelected(false);
        trackMapCheckBox.setEnabled(false);
        addComponent(trackMapCheckBox);
        trackMapLabel.setPosition(60, LINE_HEIGHT*4);
        trackMapLabel.setSize(300,LINE_HEIGHT);
        addComponent(trackMapLabel);
    }
    
    @Override
    public void onEnabled(){
        if(isEnabled()){
            liveTimingCheckBox.setEnabled(true);
            loggingCheckBox.setEnabled(true);
            incidentLogCheckBox.setEnabled(true);
        }else{
            liveTimingCheckBox.setEnabled(false);
            loggingCheckBox.setEnabled(false);
            incidentLogCheckBox.setEnabled(false);
        }
    }

    public static GeneralExtentionConfigPanel getInstance() {
        if (instance == null) {
            instance = new GeneralExtentionConfigPanel();
        }
        return instance;
    }
    
    public boolean isLiveTimingEnabled(){
        return liveTimingCheckBox.isSelected();
    }
    
    public boolean isIncidentLogEnabled(){
        return incidentLogCheckBox.isSelected();
    }
    
    public boolean isLoggingEnabled(){
        return loggingCheckBox.isSelected();
    }

}
