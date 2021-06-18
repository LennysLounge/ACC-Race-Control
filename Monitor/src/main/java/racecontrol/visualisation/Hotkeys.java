/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.visualisation;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import processing.event.KeyEvent;
import racecontrol.client.AccBroadcastingClient;

/**
 *
 * @author Leonard
 */
public class Hotkeys {

    public static final Logger LOG = Logger.getLogger(Hotkeys.class.getName());

    public static final int KEY_1 = 49;
    public static final int KEY_2 = 50;
    public static final int KEY_3 = 51;
    public static final int KEY_F1 = 112;
    public static final int KEY_F2 = 113;
    public static final int KEY_F3 = 114;
    public static final int KEY_F6 = 117;

    private AccBroadcastingClient client;

    public Hotkeys() {
    }

    public void setClient(AccBroadcastingClient client) {
        this.client = client;
    }

    public void handleHotkeys(KeyEvent event) {
        if (client == null) {
            return;
        }

        switch (event.getKeyCode()) {
            case KEY_1:
                client.sendSetCameraRequest("Drivable", "Cockpit");
                break;
            case KEY_2:
                client.sendSetCameraRequest("Drivable", "Bonnet");
                break;
            case KEY_3:
                client.sendSetCameraRequest("Drivable", "Chase");
                break;
            case KEY_F1:
                List<String> cameras = Arrays.asList("Cockpit", "Dash", "Helmet", "Bonnet", "DashPro", "Chase", "FarChase");
                String activeCamera = client.getModel().getSessionInfo().getActiveCamera();
                int index = cameras.indexOf(activeCamera);
                client.sendSetCameraRequest("Drivable", cameras.get((index + 1) % cameras.size()));
                break;
            case KEY_F2:
                List<String> hudPages = Arrays.asList("Blank", "Basic HUD", "Help", "TimeTable", "Broadcasting", "TrackMap");
                String activeHud = client.getModel().getSessionInfo().getCurrentHudPage();
                index = hudPages.indexOf(activeHud);
                client.sendSetHudPageRequest(hudPages.get((index + 1) % hudPages.size()));
                break;
            case KEY_F3:
                //camera set "setVR" is not used and will default switch between
                //set1 set2 helicam and pitlane. We can use this to emulate
                //the F3 functionality.
                client.sendSetCameraRequest("setVR", "-");
                break;
            case KEY_F6:
                cameras = Arrays.asList("Onboard0", "Onboard1", "Onboard2", "Onboard3");
                activeCamera = client.getModel().getSessionInfo().getActiveCamera();
                index = cameras.indexOf(activeCamera);
                client.sendSetCameraRequest("Onboard", cameras.get((index + 1) % cameras.size()));
                break;
        }

    }
}
