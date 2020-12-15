/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.fullcourseyellow;

import base.screen.visualisation.LookAndFeel;
import base.screen.visualisation.gui.LPButton;
import base.screen.visualisation.gui.LPContainer;
import base.screen.visualisation.gui.LPTable;

/**
 *
 * @author Leonard
 */
public class FullCourseYellowPanel extends LPContainer {

    private final FullCourseYellowExtension extension;

    private final LPButton startFCYButton = new LPButton("Start FCY");
    private final LPButton stopFCYButton = new LPButton("Stop FCY");
    private final LPTable carSpeedTable = new LPTable();

    public FullCourseYellowPanel(FullCourseYellowExtension extension) {
        setName("FCC");
        this.extension = extension;

        startFCYButton.setSize(200, LookAndFeel.LINE_HEIGHT);
        startFCYButton.setAction(() -> {
            extension.startFCY();
        });
        stopFCYButton.setSize(200, LookAndFeel.LINE_HEIGHT);
        stopFCYButton.setAction(() -> {
            extension.stopFCY();
        });

        carSpeedTable.setOverdrawForLastLine(true);
        carSpeedTable.setTableModel(extension.getTableModel());

        addComponent(startFCYButton);
        addComponent(stopFCYButton);
        addComponent(carSpeedTable);
    }

    @Override
    public void draw() {
        if (extension.isFCY()) {
            applet.fill(LookAndFeel.COLOR_YELLOW);
        } else {
            applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        }
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        if (extension.isFCY()) {
            applet.fill(LookAndFeel.COLOR_YELLOW);
            applet.rect(430, 0, getWidth() - 430, LookAndFeel.LINE_HEIGHT);
        }
    }

    @Override
    public void onResize(int w, int h) {
        startFCYButton.setPosition(10, 0);
        stopFCYButton.setPosition(220, 0);
        carSpeedTable.setSize(getWidth(), getHeight() - LookAndFeel.LINE_HEIGHT);
        carSpeedTable.setPosition(0, LookAndFeel.LINE_HEIGHT);

        extension.setColumnCount(Math.max(1, (int) Math.floor(getWidth() / 450)));

    }

}
