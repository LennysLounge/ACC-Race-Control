/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.fullcourseyellow;

import ACCLiveTiming.monitor.visualisation.LookAndFeel;
import ACCLiveTiming.monitor.visualisation.gui.LPButton;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import ACCLiveTiming.monitor.visualisation.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class FullCourseYellowPanel extends LPContainer {

    private final FullCourseYellowExtension extension;
    
    private final LPButton startFCYButton = new LPButton("Start FCY");
    private final LPButton stopFCYButton = new LPButton("Stop FCY");
    private final LPTable carSpeedTable = new LPTable();
    private final CarSpeedTableModel tableModel;

    public FullCourseYellowPanel(FullCourseYellowExtension extension) {
        setName("FCY");
        this.extension = extension;
        tableModel = extension.getTableModel();
        
        startFCYButton.setSize(200, LookAndFeel.LINE_HEIGHT);
        startFCYButton.setAction(()->{
            tableModel.setFCYActive(true);
        });
        stopFCYButton.setSize(200, LookAndFeel.LINE_HEIGHT);
        stopFCYButton.setAction(()->{
            tableModel.setFCYActive(false);
        });
        
        carSpeedTable.setOverdrawForLastLine(true);
        carSpeedTable.setTableModel(tableModel);
        
        addComponent(startFCYButton);
        addComponent(stopFCYButton);
        addComponent(carSpeedTable);
    }

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(int w, int h) {
        startFCYButton.setPosition(10, 0);
        stopFCYButton.setPosition(220, 0);
        carSpeedTable.setSize(getWidth(), getHeight()-LookAndFeel.LINE_HEIGHT);
        carSpeedTable.setPosition(0, LookAndFeel.LINE_HEIGHT);
        
        tableModel.setColumnCount(Math.max(1,(int)Math.floor(getWidth() / 450)));
    }

}
