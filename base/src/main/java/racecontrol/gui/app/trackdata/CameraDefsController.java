/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.trackdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static racecontrol.client.AccBroadcastingClient.getClient;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.TrackInfoEvent;
import racecontrol.client.model.Model;
import racecontrol.client.protocol.SessionInfo;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class CameraDefsController
        implements EventListener {

    private final CameraDefsPanel panel = new CameraDefsPanel();

    private Map<String, Map<String, List<Float>>> camChanges = new HashMap<>();

    private String currentCamSet = "";
    private String currentCam = "";
    private int currentFocusedCar = 0;

    public CameraDefsController() {
        EventBus.register(this);

        camChanges.put("set1", new HashMap<>());
        camChanges.put("set2", new HashMap<>());
        panel.camChanges = camChanges;
    }

    public CameraDefsPanel getPanel() {
        return panel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            SessionInfo info = ((RealtimeUpdateEvent) e).getSessionInfo();

            float currentSplinePos = 0;
            Model model = getClient().getModel();
            if (model.hasCarWithIndex(info.getFocusedCarIndex())) {
                currentSplinePos = model.getCar(info.getFocusedCarIndex())
                        .map(car -> car.splinePosition)
                        .orElse(0f);
            }

            panel.currentSplinePos = currentSplinePos;

            panel.currentCamSet.setText(info.getActiveCameraSet());
            panel.currentCam.setText(info.getActiveCamera());
            panel.invalidate();

            logCamChanges(info, currentSplinePos);

            currentCamSet = info.getActiveCameraSet();
            currentCam = info.getActiveCamera();
            currentFocusedCar = info.getFocusedCarIndex();
        } else if (e instanceof TrackInfoEvent) {
            Map<String, List<String>> camSets = ((TrackInfoEvent) e).getInfo().getCameraSets();
            panel.camSets = camSets;
            camSets.get("set1").forEach(cam -> camChanges.get("set1").put(cam, new ArrayList<>()));
            camSets.get("set2").forEach(cam -> camChanges.get("set2").put(cam, new ArrayList<>()));
        }
    }

    private void logCamChanges(SessionInfo info, float currentSplinePos) {

        // if the focus changed we dont want to log the cam change.
        if (currentFocusedCar != info.getFocusedCarIndex()) {
            return;
        }

        // if the cam set changed we dont want to log.
        String newCamSet = info.getActiveCameraSet();
        if (!newCamSet.equals(currentCamSet)) {
            return;
        }

        // if the cam set is not "set1" or "set2" then dont log
        if (!newCamSet.equals("set1") && !newCamSet.equals("set2")) {
            return;
        }

        // if the new cam did not change we dont log.
        String newCam = info.getActiveCamera();
        if (newCam.equals(currentCam)) {
            return;
        }

        // camera did change. Record current car position
        if (camChanges.get(newCamSet).containsKey(newCam)) {
            camChanges.get(newCamSet).get(newCam).add(currentSplinePos);
        }
    }

}
