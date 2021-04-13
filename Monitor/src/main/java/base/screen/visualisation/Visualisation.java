/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.visualisation;

import base.ACCLiveTimingExtensionFactory;
import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.components.BasePanel;
import base.screen.networking.AccBroadcastingClient;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * The base for the processing visualization.
 *
 * @author Leonard
 */
public class Visualisation extends CustomPApplet {

    /**
     * This classes logger.
     */
    private static final Logger LOG = Logger.getLogger(Visualisation.class.getName());
    /**
     * Timer since the last draw.
     */
    private int timer = 0;
    /**
     * Connection client.
     */
    private AccBroadcastingClient client;
    /**
     * The base panel to use.
     */
    private BasePanel basePanel;
    /**
     * List of all registered extension modules.
     */
    private static final List<ACCLiveTimingExtensionFactory> modules = new LinkedList<>();
    /**
     * List of client extensions.
     */
    private static final List<AccClientExtension> extensions = new LinkedList<>();

    /**
     * Creates a new instance of this object.
     *
     * @param client The ACC client connection to use.
     */
    public Visualisation(AccBroadcastingClient client) {
        this.client = client;
        loadModules();
    }

    @Override
    public void settings() {
        size(1600, 900);
    }

    @Override
    public void setup() {
        LookAndFeel.init(this);
        surface.setResizable(true);
        surface.setTitle("ACC Accident Tracker");
        frameRate(30);

        //init components.
        basePanel = new BasePanel(client);
        setComponent(basePanel);
        
        /*
        //create extensions
        for (ACCLiveTimingExtensionFactory module : modules) {
            AccClientExtension extension = module.createExtension();
            if (extension != null) {
                extensions.add(extension);
            }
        }
        //Add panels to basePanel
        basePanel.addExtensionPanels(extensions.stream()
                .map(extension -> extension.getPanel())
                .filter(panel -> panel != null)
                .collect(Collectors.toList()));
        */
    }

    @Override
    public void draw() {
        int dt = (int) (1000 / frameRate);
        timer += dt;
        if (timer > client.getUpdateInterval() || forceRedraw) {
            basePanel.updateHeader();
        }

        super.draw();
    }

    @Override
    public void keyPressed() {
        if (key == ESC) {
            key = 0;
        }
        super.keyPressed();
    }

    @Override
    public void exit() {
        LOG.info("Stopping Visualisation");
        //stop the client connection.
        //client.sendUnregisterRequest();

        //client.stopAndKill();
        super.exit();
    }

    public void exitExplicit() {
        super.exit();
    }

    private void loadModules() {
        ServiceLoader.load(ACCLiveTimingExtensionFactory.class).forEach(module -> {
            LOG.info("Loading extension " + module.getName());
            modules.add(module);
        });
    }
    
    public static List<ACCLiveTimingExtensionFactory> getModules(){
        return modules;
    }
}
