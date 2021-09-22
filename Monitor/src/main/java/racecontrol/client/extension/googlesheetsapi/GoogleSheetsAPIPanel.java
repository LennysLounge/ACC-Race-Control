/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.googlesheetsapi;


import processing.core.PApplet;
import racecontrol.utility.TimeUtils;
import racecontrol.gui.LookAndFeel;
import racecontrol.gui.lpui.LPButton;
import racecontrol.gui.lpui.LPContainer;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;



/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIPanel extends LPContainer {

    private final GoogleSheetsAPIController extension;

    private final LPButton setToPractice = new LPButton("Target \"Practice!\"");
    private final LPButton setToQuali = new LPButton("Target \"Qualifying!\"");
    private final LPButton setToRace1 = new LPButton("target \"Race 1!\"");
    private final LPButton setToRace2 = new LPButton("target \"Race 2!\"");
    private final LPButton sendEmptyIncident = new LPButton("Send empty incident");

    public GoogleSheetsAPIPanel(GoogleSheetsAPIController extension) {
        this.extension = extension;
        setName("Overview");

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
    public void draw(PApplet applet) {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.fill(255);
        applet.textAlign(LEFT, CENTER);
        applet.text("Current sheet target: " + extension.getCurrentTargetSheet(),
                10, LookAndFeel.LINE_HEIGHT * 0.5f);

        String offset = "";
        if (extension.isGreenFlagOffsetBeeingMeasured()) {
            long diff = System.currentTimeMillis() - extension.getGreenFlagTimeStamp();
            offset += TimeUtils.asDuration((int)diff);
        } else {
            offset += "-";
        }
        applet.text("Replay offset: " + offset,
                350, LookAndFeel.LINE_HEIGHT * 0.5f);

    }

    @Override
    public void onResize(float w, float h) {
        setToPractice.setPosition(10, 40);
        setToQuali.setPosition(10, 80);
        setToRace1.setPosition(10, 120);
        setToRace2.setPosition(10, 160);
        sendEmptyIncident.setPosition(10, 300);
    }

}
