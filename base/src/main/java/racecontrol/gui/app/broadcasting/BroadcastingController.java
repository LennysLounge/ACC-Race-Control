/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.broadcasting;

import java.util.logging.Logger;
import racecontrol.client.extension.broadcastingoverlay.BroadcastingOverlayExtension;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class BroadcastingController {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(BroadcastingController.class.getName());
    /**
     * Panel for this controller.
     */
    private final BroadcastingPanel panel;
    /**
     * Reference to the overlay extension.
     */
    private final BroadcastingOverlayExtension overlayExtension;

    public BroadcastingController() {
        this.panel = new BroadcastingPanel();
        overlayExtension = BroadcastingOverlayExtension.getInstance();

        panel.enableCheckBox.setChangeAction((state) -> {
            if (state) {
                overlayExtension.startBroadcastingOverlay();
            } else {
                overlayExtension.stopBroadcastingOverlay();
            }
        });
    }

    public LPContainer getPanel() {
        return panel;
    }
}
