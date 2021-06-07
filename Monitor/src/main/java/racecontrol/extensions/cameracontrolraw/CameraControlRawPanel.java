/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.extensions.cameracontrolraw;

import static racecontrol.visualisation.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.visualisation.LookAndFeel.LINE_HEIGHT;
import racecontrol.visualisation.gui.LPButton;
import racecontrol.visualisation.gui.LPContainer;
import racecontrol.visualisation.gui.LPLabel;
import racecontrol.visualisation.gui.LPTextField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Leonard
 */
public class CameraControlRawPanel
        extends LPContainer {

    private final Map<String, Map<String, LPButton>> cameraButtons = new HashMap<>();
    private final Map<String, LPButton> hudButtons = new HashMap<>();
    private final CameraControlRawExtension extension;

    private final LPLabel instantReplayLabel = new LPLabel("Instant Replay");
    private final LPButton instantReplay60Button = new LPButton("-60s");
    private final LPButton instantReplay30Button = new LPButton("-30s");
    private final LPButton instantReplay15Button = new LPButton("-15s");
    private final LPLabel instantReplayCustomLabel = new LPLabel("Seconds back:");
    private final LPTextField instantReplayBackTextField = new LPTextField();
    private final LPLabel instantReplayDurationLabel = new LPLabel("Duration:");
    private final LPTextField instantReplayDurationTextField = new LPTextField();
    private final LPButton instantReplayCustomButton = new LPButton("Custom");

    private final LPLabel hudPageLabel = new LPLabel("HUD");
    private final LPLabel cameraLabel = new LPLabel("Camera");

    public CameraControlRawPanel(CameraControlRawExtension extension) {
        setName("CAMERA");
        this.extension = extension;

        instantReplayLabel.setPosition(20, 0);
        addComponent(instantReplayLabel);
        instantReplay60Button.setPosition(20, LINE_HEIGHT);
        instantReplay60Button.setSize(60, LINE_HEIGHT);
        instantReplay60Button.setAction(() -> extension.startInstantReplay(60, 60));
        addComponent(instantReplay60Button);
        instantReplay30Button.setPosition(90, LINE_HEIGHT);
        instantReplay30Button.setSize(60, LINE_HEIGHT);
        instantReplay30Button.setAction(() -> extension.startInstantReplay(30, 30));
        addComponent(instantReplay30Button);
        instantReplay15Button.setPosition(160, LINE_HEIGHT);
        instantReplay15Button.setSize(60, LINE_HEIGHT);
        instantReplay15Button.setAction(() -> extension.startInstantReplay(15, 15));
        addComponent(instantReplay15Button);
        instantReplayCustomLabel.setPosition(230, LINE_HEIGHT);
        addComponent(instantReplayCustomLabel);
        instantReplayBackTextField.setPosition(370, LINE_HEIGHT);
        instantReplayBackTextField.setSize(70, LINE_HEIGHT);
        instantReplayBackTextField.setValue("60");
        addComponent(instantReplayBackTextField);
        instantReplayDurationLabel.setPosition(450, LINE_HEIGHT);
        addComponent(instantReplayDurationLabel);
        instantReplayDurationTextField.setPosition(540, LINE_HEIGHT);
        instantReplayDurationTextField.setSize(70, LINE_HEIGHT);
        instantReplayDurationTextField.setValue("15");
        addComponent(instantReplayDurationTextField);

        instantReplayCustomButton.setPosition(620, LINE_HEIGHT);
        instantReplayCustomButton.setSize(100, LINE_HEIGHT);
        instantReplayCustomButton.setAction(() -> {
            try {
                int seconds = Integer.parseInt(instantReplayBackTextField.getValue());
                int duration = Integer.parseInt(instantReplayDurationTextField.getValue());
                extension.startInstantReplay(seconds, duration);
            } catch (Exception e) {

            }
        });
        addComponent(instantReplayCustomButton);

        hudPageLabel.setPosition(20, LINE_HEIGHT * 3);
        addComponent(hudPageLabel);
        cameraLabel.setPosition(240, LINE_HEIGHT * 3);
        addComponent(cameraLabel);
    }

    @Override
    public void draw() {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    public void setCameraSets(Map<String, List<String>> sets) {
        //remove the old buttons
        cameraButtons.values().forEach(
                list -> list.values().forEach(button -> removeComponent(button))
        );
        cameraButtons.clear();

        int row = 4;
        int column = 0;
        int buttonWidth = 150;
        int buttonOffset = 240;
        for (String camSet : sets.keySet()) {
            if (camSet.isEmpty()) {
                continue;
            }
            Map<String, LPButton> buttons = new HashMap<>();
            row = 4;
            for (String camera : sets.get(camSet)) {
                LPButton b = new LPButton(camera);
                b.setPosition(buttonOffset + (20 + buttonWidth) * column, LINE_HEIGHT * row++);
                b.setSize(buttonWidth, LINE_HEIGHT);
                b.setAction(() -> {
                    extension.setCameraSet(camSet, camera);
                });
                buttons.put(camera, b);
                addComponent(b);
            }
            column++;
            cameraButtons.put(camSet, buttons);
        }
        invalidate();
    }

    public void setHUDPages(List<String> pages) {
        hudButtons.values().forEach(b -> removeComponent(b));
        hudButtons.clear();

        int row = 4;
        for (String page : pages) {
            if (page.isEmpty()) {
                continue;
            }
            LPButton b = new LPButton(page);
            b.setPosition(20, LINE_HEIGHT * row++);
            b.setSize(200, LINE_HEIGHT);
            b.setAction(() -> {
                extension.setHudPage(page);
            });
            hudButtons.put(page, b);
            addComponent(b);
        }
        invalidate();
    }

    public void setActiveCameraSet(String activeCameraSet, String activeCamera) {
        if (cameraButtons.containsKey(activeCameraSet)) {
            cameraButtons.values().forEach(
                    list -> list.values().forEach(button -> button.setEnabled(true))
            );
            cameraButtons.get(activeCameraSet).get(activeCamera).setEnabled(false);
        }
    }

    public void setActiveHudPage(String page) {
        if (hudButtons.containsKey(page)) {
            hudButtons.values().forEach(button -> button.setEnabled(true));
            hudButtons.get(page).setEnabled(false);
        }
    }
}
