/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting;

import racecontrol.lpgui.gui.LPCollapsablePanel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import static racecontrol.LookAndFeel.LINE_HEIGHT;
import racecontrol.lpgui.gui.LPButton;
import racecontrol.lpgui.gui.LPContainer;
import racecontrol.lpgui.gui.LPLabel;
import racecontrol.lpgui.gui.LPTextField;

/**
 *
 * @author Leonard
 */
public class BroadcastingPanel
        extends LPContainer {

    public static final Logger LOG = Logger.getLogger(BroadcastingPanel.class.getName());

    /**
     * Extension.
     */
    private final BroadcastingExtension extension;
    /**
     * The panel of the live timing extension.
     */
    private final LPContainer liveTimingPanel;

    private final LPLabel hudLabel = new LPLabel("HUD");
    private final LPLabel cameraLable = new LPLabel("Camera");
    private final LPLabel cameraExtraLable = new LPLabel("--");

    private final Map<String, LPButton> hudButtons = new LinkedHashMap<>();
    private final Map<String, Map<String, LPButton>> cameraButtonsRef = new HashMap<>();
    private final List<LPButton> carCameraButtons = new LinkedList<>();
    private final List<LPButton> tvCameraButtons = new LinkedList<>();

    private final LPLabel instantReplayLabel = new LPLabel("Instant Replay");
    private final LPButton instantReplay60Button = new LPButton("-60s");
    private final LPButton instantReplay30Button = new LPButton("-30s");
    private final LPButton instantReplay15Button = new LPButton("-15s");
    private final LPTextField instantReplayBackTextField = new LPTextField();
    private final LPLabel instantReplayCustomLabel = new LPLabel("seconds back, for");
    private final LPTextField instantReplayDurationTextField = new LPTextField();
    private final LPLabel instantReplayDurationLabel = new LPLabel("seconds.");
    private final LPButton instantReplayCustomButton = new LPButton("Go");

    private final LPCollapsablePanel collapsablePanel = new LPCollapsablePanel("Broadcasting control");

    public BroadcastingPanel(BroadcastingExtension extension,
            LPContainer liveTimingPanel) {
        this.extension = extension;
        this.liveTimingPanel = liveTimingPanel;

        setName("BROADCASTING");

        addComponent(liveTimingPanel);

        collapsablePanel.setAction(() -> {
            onResize((int) getWidth(), (int) getHeight());
        });
        addComponent(collapsablePanel);

        hudLabel.setSize(200, LINE_HEIGHT);
        collapsablePanel.addComponent(hudLabel);
        cameraLable.setSize(100, LINE_HEIGHT);
        collapsablePanel.addComponent(cameraLable);
        cameraExtraLable.setSize(200, LINE_HEIGHT);
        collapsablePanel.addComponent(cameraExtraLable);
        //addComponent(replayLabel);

        addHUDButton("Basic", "Basic HUD");
        addHUDButton("Blank", "Blank");
        //addHUDButton("Help", "Help");
        addHUDButton("Times", "TimeTable");
        addHUDButton("BC", "Broadcasting");
        addHUDButton("Map", "TrackMap");

        addCarCameraButton("Bumper", "Drivable", "DashPro");
        addCarCameraButton("Bonnet", "Drivable", "Bonnet");
        addCarCameraButton("Wing", "Onboard", "Onboard3");
        //addCarCameraButton("Dash", "Drivable", "Dash");
        //addCarCameraButton("Cockpit", "Drivable", "Cockpit");
        //addCarCameraButton("Helmet", "Drivable", "Helmet");
        addCarCameraButton("Interior", "Onboard", "Onboard0");
        addCarCameraButton("Driver", "Onboard", "Onboard1");
        addCarCameraButton("Passenger", "Onboard", "Onboard2");
        //addCarCameraButton("Chase", "Drivable", "Chase");
        //addCarCameraButton("Far Chase", "Drivable", "FarChase");
        

        instantReplayLabel.setSize(200, LINE_HEIGHT);
        collapsablePanel.addComponent(instantReplayLabel);

        instantReplay60Button.setSize(60, LINE_HEIGHT);
        instantReplay60Button.setAction(() -> extension.startInstantReplay(60, 60));
        collapsablePanel.addComponent(instantReplay60Button);
        instantReplay30Button.setSize(60, LINE_HEIGHT);
        instantReplay30Button.setAction(() -> extension.startInstantReplay(30, 30));
        collapsablePanel.addComponent(instantReplay30Button);
        instantReplay15Button.setSize(60, LINE_HEIGHT);
        instantReplay15Button.setAction(() -> extension.startInstantReplay(15, 15));
        collapsablePanel.addComponent(instantReplay15Button);

        instantReplayBackTextField.setSize(60, LINE_HEIGHT);
        instantReplayBackTextField.setValue("60");
        collapsablePanel.addComponent(instantReplayBackTextField);
        instantReplayCustomLabel.setSize(160, LINE_HEIGHT);
        collapsablePanel.addComponent(instantReplayCustomLabel);
        instantReplayDurationTextField.setSize(60, LINE_HEIGHT);
        instantReplayDurationTextField.setValue("15");
        collapsablePanel.addComponent(instantReplayDurationTextField);
        instantReplayDurationLabel.setSize(90, LINE_HEIGHT);
        collapsablePanel.addComponent(instantReplayDurationLabel);
        instantReplayCustomButton.setSize(78, LINE_HEIGHT);
        instantReplayCustomButton.setAction(() -> {
            try {
                int seconds = Integer.parseInt(instantReplayBackTextField.getValue());
                int duration = Integer.parseInt(instantReplayDurationTextField.getValue());
                extension.startInstantReplay(seconds, duration);
            } catch (Exception e) {

            }
        });
        collapsablePanel.addComponent(instantReplayCustomButton);
         
    }

    @Override
    public void onResize(int w, int h) {
        int broadcastingHight = LINE_HEIGHT;
        if (!collapsablePanel.isCollapsed()) {
            broadcastingHight = (int) (LINE_HEIGHT * 7.5f);
        }

        int tableHeight = Math.max(LINE_HEIGHT * 2, h - broadcastingHight);
        liveTimingPanel.setPosition(0, 0);
        liveTimingPanel.setSize(w, tableHeight);

        collapsablePanel.setSize(w, h - tableHeight);
        collapsablePanel.setPosition(0, tableHeight);

        hudLabel.setPosition(20, LINE_HEIGHT);
        cameraLable.setPosition(210, LINE_HEIGHT);
        cameraExtraLable.setPosition(210 + cameraLable.getWidth(), LINE_HEIGHT);

        int x = 20;
        int y = 2;
        for (LPButton button : hudButtons.values()) {
            button.setPosition(x, LINE_HEIGHT * y);
            y++;
        }

        x = 210;
        y = 2;
        for (LPButton button : tvCameraButtons) {
            button.setPosition(x, LINE_HEIGHT * y);
            y++;
        }

        x = 364;
        y = 2;
        for (LPButton button : carCameraButtons) {
            button.setPosition(x, LINE_HEIGHT * y);
            y++;
            if (y > 4) {
                y = 2;
                x += button.getWidth() + 4;
            }
        }
        

        if ((x + 516) > w) {
            positionInstantReplayElements(210, 5);
            instantReplayLabel.setVisible(false);
        } else {
            instantReplayLabel.setVisible(true);
            instantReplayLabel.setPosition(x + 36, LINE_HEIGHT);
            positionInstantReplayElements(x + 36, 2);
        }
    }

    private int positionInstantReplayElements(int X, float y) {
        int x = X;
        instantReplay60Button.setPosition(x, y * LINE_HEIGHT);
        x += instantReplay60Button.getWidth() + 4;
        instantReplay30Button.setPosition(x, y * LINE_HEIGHT);
        x += instantReplay30Button.getWidth() + 4;
        instantReplay15Button.setPosition(x, y * LINE_HEIGHT);

        x = X;
        instantReplayBackTextField.setPosition(x, (y + 1) * LINE_HEIGHT);
        x += instantReplayBackTextField.getWidth() + 5;
        instantReplayCustomLabel.setPosition(x, (y + 1) * LINE_HEIGHT);
        x += instantReplayCustomLabel.getWidth();
        instantReplayDurationTextField.setPosition(x, (y + 1) * LINE_HEIGHT);
        x += instantReplayDurationTextField.getWidth() + 5;
        instantReplayDurationLabel.setPosition(x, (y + 1) * LINE_HEIGHT);
        x += instantReplayDurationLabel.getWidth();
        instantReplayCustomButton.setPosition(x, (y + 1) * LINE_HEIGHT);
        x += instantReplayCustomButton.getWidth();
        return x;
    }

    public void setCameraSets(Map<String, List<String>> sets) {
        for (String camSet : sets.keySet()) {
            if (!cameraButtonsRef.containsKey(camSet)) {
                cameraButtonsRef.put(camSet, new LinkedHashMap<>());
            }
            if (camSet.equals("set2")
                    || camSet.equals("set1")
                    || camSet.equals("Helicam")) {
                String name = "";
                if (camSet.equals("set1")) {
                    name = "TV 1";
                } else if (camSet.equals("set2")) {
                    name = "TV 2";
                } else if (camSet.equals("Helicam")) {
                    name = "Helicam";
                } else if (camSet.equals("pitlane")) {
                    name = "PIT";
                }
                LPButton b = new LPButtonCustom(name);
                b.setAction(() -> extension.setCameraSet(camSet, sets.get(camSet).get(0)));
                b.setSize(150, LINE_HEIGHT);
                collapsablePanel.addComponent(b);
                for (String camera : sets.get(camSet)) {
                    cameraButtonsRef.get(camSet).put(camera, b);
                }
                tvCameraButtons.add(b);
            }
        }
    }

    private void addHUDButton(String name, String page) {
        LPButton button = new LPButtonCustom(name);
        button.setAction(() -> extension.setHudPage(page));
        button.setSize(150, LINE_HEIGHT);
        hudButtons.put(page, button);
        collapsablePanel.addComponent(button);
    }

    private void addCarCameraButton(String name, String camSet, String camera) {
        LPButton b = new LPButtonCustom(name);
        b.setAction(() -> extension.setCameraSet(camSet, camera));
        b.setSize(150, LINE_HEIGHT);
        collapsablePanel.addComponent(b);
        if (!cameraButtonsRef.containsKey(camSet)) {
            cameraButtonsRef.put(camSet, new HashMap<>());
        }
        cameraButtonsRef.get(camSet).put(camera, b);
        carCameraButtons.add(b);
    }

    public void setActiveCameraSet(String activeCameraSet, String activeCamera) {
        //If the active camera is a pitlane camera we pretend like it is actuall
        //a camera from set1.
        if (activeCameraSet.equals("pitlane")) {
            if (cameraButtonsRef.containsKey("set1")) {
                if (cameraButtonsRef.get("set1").size() > 0) {
                    activeCameraSet = "set1";
                    activeCamera = cameraButtonsRef.get("set1").keySet().stream().findFirst().get();
                }
            }
        }

        if (cameraButtonsRef.containsKey(activeCameraSet)) {
            cameraButtonsRef.values().forEach(
                    list -> list.values().forEach(button -> button.setEnabled(true))
            );
            if (cameraButtonsRef.get(activeCameraSet).containsKey(activeCamera)) {
                cameraButtonsRef.get(activeCameraSet).get(activeCamera).setEnabled(false);
                cameraExtraLable.setText("");
                cameraExtraLable.setSize(200, LINE_HEIGHT);
            } else {
                cameraExtraLable.setText(activeCameraSet + " - " + activeCamera);
            }
        }
    }

    public void setActiveHudPage(String page) {
        if (hudButtons.containsKey(page)) {
            hudButtons.values().forEach(button -> button.setEnabled(true));
            hudButtons.get(page).setEnabled(false);
        }
    }

}
