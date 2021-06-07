/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.broadcasting;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.extensions.livetiming.tablemodels.LiveTimingTableModel;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import racecontrol.visualisation.gui.LPButton;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPLabel;
import racecontrol.visualisation.gui.LPTextField;

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

    public BroadcastingPanel(BroadcastingExtension extension,
            LPContainer liveTimingPanel) {
        this.extension = extension;
        this.liveTimingPanel = liveTimingPanel;

        setName("BROADCASTING");

        addComponent(liveTimingPanel);

        hudLabel.setSize(200, LINE_HEIGHT);
        addComponent(hudLabel);
        cameraLable.setSize(100, LINE_HEIGHT);
        addComponent(cameraLable);
        cameraExtraLable.setSize(200, LINE_HEIGHT);
        addComponent(cameraExtraLable);
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
        addComponent(instantReplayLabel);

        instantReplay60Button.setSize(60, LINE_HEIGHT);
        instantReplay60Button.setAction(() -> extension.startInstantReplay(60, 60));
        addComponent(instantReplay60Button);
        instantReplay30Button.setSize(60, LINE_HEIGHT);
        instantReplay30Button.setAction(() -> extension.startInstantReplay(30, 30));
        addComponent(instantReplay30Button);
        instantReplay15Button.setSize(60, LINE_HEIGHT);
        instantReplay15Button.setAction(() -> extension.startInstantReplay(15, 15));
        addComponent(instantReplay15Button);

        instantReplayBackTextField.setSize(60, LINE_HEIGHT);
        instantReplayBackTextField.setValue("60");
        addComponent(instantReplayBackTextField);
        instantReplayCustomLabel.setSize(160, LINE_HEIGHT);
        addComponent(instantReplayCustomLabel);
        instantReplayDurationTextField.setSize(60, LINE_HEIGHT);
        instantReplayDurationTextField.setValue("15");
        addComponent(instantReplayDurationTextField);
        instantReplayDurationLabel.setSize(90, LINE_HEIGHT);
        addComponent(instantReplayDurationLabel);
        instantReplayCustomButton.setSize(78, LINE_HEIGHT);
        instantReplayCustomButton.setAction(() -> {
            try {
                int seconds = Integer.parseInt(instantReplayBackTextField.getValue());
                int duration = Integer.parseInt(instantReplayDurationTextField.getValue());
                extension.startInstantReplay(seconds, duration);
            } catch (Exception e) {

            }
        });
        addComponent(instantReplayCustomButton);
    }

    @Override
    public void onResize(int w, int h) {
        int tableHeight = (int) Math.max(2, Math.floor(h / LINE_HEIGHT) - 7);
        liveTimingPanel.setPosition(0, 0);
        liveTimingPanel.setSize(w, tableHeight * LINE_HEIGHT);

        hudLabel.setPosition(20, LINE_HEIGHT * tableHeight);
        cameraLable.setPosition(210, LINE_HEIGHT * tableHeight);
        cameraExtraLable.setPosition(210 + cameraLable.getWidth(), LINE_HEIGHT * tableHeight);

        int x = 20;
        int y = 1;
        for (LPButton button : hudButtons.values()) {
            button.setPosition(x, (tableHeight + y) * LINE_HEIGHT);
            y++;
        }

        x = 210;
        y = 1;
        for (LPButton button : tvCameraButtons) {
            button.setPosition(x, (tableHeight + y) * LINE_HEIGHT);
            y++;
        }

        x = 364;
        y = 1;
        for (LPButton button : carCameraButtons) {
            button.setPosition(x, (tableHeight + y) * LINE_HEIGHT);
            y++;
            if (y > 3) {
                y = 1;
                x += button.getWidth() + 4;
            }
        }

        if ((x + 516) > w) {
            positionInstantReplayElements(210, tableHeight + 4);
            instantReplayLabel.setVisible(false);
        } else {
            instantReplayLabel.setVisible(true);
            instantReplayLabel.setPosition(x + 36, tableHeight * LINE_HEIGHT);
            positionInstantReplayElements(x + 36, tableHeight + 1);
        }

    }

    private void positionInstantReplayElements(int X, int y) {
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
                addComponent(b);
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
        addComponent(button);
    }

    private void addCarCameraButton(String name, String camSet, String camera) {
        LPButton b = new LPButtonCustom(name);
        b.setAction(() -> extension.setCameraSet(camSet, camera));
        b.setSize(150, LINE_HEIGHT);
        addComponent(b);
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
