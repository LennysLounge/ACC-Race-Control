/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.settings;

import racecontrol.utility.Version;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import processing.core.PApplet;
import racecontrol.gui.CustomPApplet;
import static racecontrol.gui.LookAndFeel.TEXT_SIZE;
import racecontrol.gui.app.Menu;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.PageController;
import racecontrol.gui.app.settings.connection.ConnectionController;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPScrollPanel;
import racecontrol.persistance.PersistantConfig;
import static racecontrol.persistance.PersistantConfigKeys.ENABLE_EXPERIMENTAL_FEATURES;

/**
 *
 * @author Leonard
 */
public class SettingsPage
        extends LPContainer
        implements PageController {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(SettingsPage.class.getName());

    private final LPLabel versionLabel
            = new LPLabel("Version: " + Version.VERSION);
    private final LPLabel experimentalFeaturesLabel
            = new LPLabel("Enable experimental features");
    private final LPCheckBox experimentalFeaturesCheckbox = new LPCheckBox();
    private final ChangeLogPanel changeLogPanel = new ChangeLogPanel();
    private final LPScrollPanel changeLogScrollPanel = new LPScrollPanel();
    private final LPLabel changelogLabel = new LPLabel("Changelog:");
    private final MenuItem menuItem;
    /**
     * Controller for the connection panel.
     */
    private final ConnectionController connectionController = new ConnectionController();

    public SettingsPage() {
        setName("CONFIGURATION");
        this.menuItem = new MenuItem("Settings",
                ((CustomPApplet) getApplet()).loadResourceAsPImage("/images/RC_Menu_Settings.png"));

        initComponents();
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(LookAndFeel.COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
        applet.stroke(COLOR_GRAY);
        applet.line(420, 10, 420, getHeight() - 20);
        applet.noStroke();
    }

    private void initComponents() {
        addComponent(experimentalFeaturesLabel);
        addComponent(experimentalFeaturesCheckbox);
        experimentalFeaturesCheckbox.setSelected(
                PersistantConfig.get(ENABLE_EXPERIMENTAL_FEATURES)
        );
        experimentalFeaturesCheckbox.setChangeAction(
                this::experimentalFeaturesCheckBoxChanged
        );
        addComponent(versionLabel);
        addComponent(changeLogScrollPanel);
        changeLogScrollPanel.setComponent(changeLogPanel);
        changeLogScrollPanel.setScrollbarOnRight(true);
        changeLogScrollPanel.setScrollSpeedMultiplier(2f);
        addComponent(changelogLabel);

        addComponent(connectionController.getPanel());
    }

    @Override
    public void onResize(float w, float h) {
        experimentalFeaturesLabel.setPosition(20, getHeight() - LINE_HEIGHT * 2);
        experimentalFeaturesCheckbox.setPosition(400 - LINE_HEIGHT,
                getHeight() - LINE_HEIGHT * 2 + (LINE_HEIGHT - TEXT_SIZE) / 2);
        versionLabel.setPosition(20, getHeight() - LINE_HEIGHT);

        changelogLabel.setPosition(435, 0);
        changeLogScrollPanel.setPosition(425, LINE_HEIGHT + 10);
        changeLogScrollPanel.setSize(w - 425, h - LINE_HEIGHT - 30);

        connectionController.getPanel().setPosition(0, 0);
    }

    @Override
    public LPContainer getPanel() {
        return this;
    }

    @Override
    public Menu.MenuItem getMenuItem() {
        return menuItem;
    }

    public void experimentalFeaturesCheckBoxChanged(boolean newState) {
        PersistantConfig.put(
                ENABLE_EXPERIMENTAL_FEATURES,
                !PersistantConfig.get(ENABLE_EXPERIMENTAL_FEATURES)
        );
        if (newState) {
            JOptionPane.showMessageDialog(null,
                    "To enable experimental features please restart Race Control"
            );
        }
    }

}
