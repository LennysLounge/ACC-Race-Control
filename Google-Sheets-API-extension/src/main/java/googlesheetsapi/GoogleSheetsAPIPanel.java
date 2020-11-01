/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googlesheetsapi;

import ACCLiveTiming.monitor.visualisation.LookAndFeel;
import ACCLiveTiming.monitor.visualisation.gui.LPButton;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;

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

    public GoogleSheetsAPIPanel(GoogleSheetsAPIExtension extension) {
        this.extension = extension;
        setName("Sheets API");
        
        setToPractice.setSize(200, 80);
        setToPractice.setAction(()->{
            SpreadSheetService.setTargetSheet("Practice!");
        });
        setToQuali.setSize(200, 80);
        setToQuali.setAction(()->{
            SpreadSheetService.setTargetSheet("Qualifying!");
        });
        setToRace1.setSize(200, 80);
        setToRace1.setAction(()->{
            SpreadSheetService.setTargetSheet("Race 1!");
        });
        setToRace2.setSize(200, 80);
        setToRace2.setAction(()->{
            SpreadSheetService.setTargetSheet("Race 2!");
        });
        
        addComponent(setToPractice);
        addComponent(setToQuali);
        addComponent(setToRace1);
        addComponent(setToRace2);
    }

    @Override
    public void draw() {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0,0,getWidth(), getHeight());
    }
    
    @Override
    public void onResize(int w, int h){
        setToPractice.setPosition(10, 10);
        setToQuali.setPosition(10, 110);
        setToRace1.setPosition(10, 210);
        setToRace2.setPosition(10, 310);
    }

}
