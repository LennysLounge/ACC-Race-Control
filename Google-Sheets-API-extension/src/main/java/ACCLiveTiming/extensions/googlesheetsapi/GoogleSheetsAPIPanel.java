/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.googlesheetsapi;

import ACCLiveTiming.monitor.utility.TimeUtils;
import ACCLiveTiming.monitor.visualisation.LookAndFeel;
import ACCLiveTiming.monitor.visualisation.gui.LPButton;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIPanel extends LPContainer {

    private final GoogleSheetsAPIExtension extension;

    private final LPButton setToPractice = new LPButton("Send to \"Practice!\"");
    private final LPButton setToQuali = new LPButton("Send to \"Qualifying!\"");
    private final LPButton setToRace1 = new LPButton("Send to \"Race 1!\"");
    private final LPButton setToRace2 = new LPButton("Send to \"Race 2!\"");
    private final LPButton sendEmptyIncident = new LPButton("Send empty incident");

    public GoogleSheetsAPIPanel(GoogleSheetsAPIExtension extension) {
        this.extension = extension;
        setName("Sheets API");

        setToPractice.setSize(200, LookAndFeel.LINE_HEIGHT);
        setToPractice.setAction(() -> {
            extension.setCurrentTargetSheet("Practice!");
        });
        setToQuali.setSize(200, LookAndFeel.LINE_HEIGHT);
        setToQuali.setAction(() -> {
            extension.setCurrentTargetSheet("Qualifying!");
        });
        setToRace1.setSize(200, LookAndFeel.LINE_HEIGHT);
        setToRace1.setAction(() -> {
            extension.setCurrentTargetSheet("Race 1!");
        });
        setToRace2.setSize(200, LookAndFeel.LINE_HEIGHT);
        setToRace2.setAction(() -> {
            extension.setCurrentTargetSheet("Race 2!");
        });
        
        sendEmptyIncident.setSize(200, LookAndFeel.LINE_HEIGHT);
        sendEmptyIncident.setAction(()->{
           extension.sendEmptyIncident();
        });

        addComponent(setToPractice);
        addComponent(setToQuali);
        addComponent(setToRace1);
        addComponent(setToRace2);
        addComponent(sendEmptyIncident);
    }

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(255);
        applet.textAlign(LEFT, CENTER);
        applet.text("Current sheet target: " + extension.getCurrentTargetSheet(),
                10, LookAndFeel.LINE_HEIGHT * 0.5f);

        applet.text("Is green flag offset measured: "
                + (extension.isGreenFlagOffsetBeeingMeasured() ? "True" : "False"),
                350, LookAndFeel.LINE_HEIGHT * 0.5f);
        String offset = "Offset: ";
        if (extension.isGreenFlagOffsetBeeingMeasured()) {
            long diff = System.currentTimeMillis() - extension.getGreenFlagTimeStamp();
            offset += TimeUtils.asDuration(diff);
        } else {
            offset += "-";
        }
        applet.text(offset, 350, LookAndFeel.LINE_HEIGHT * 1.5f);

    }

    @Override
    public void onResize(int w, int h) {
        setToPractice.setPosition(10, 40);
        setToQuali.setPosition(10, 80);
        setToRace1.setPosition(10, 120);
        setToRace2.setPosition(10, 160);
        sendEmptyIncident.setPosition(10, 300);
    }

}
