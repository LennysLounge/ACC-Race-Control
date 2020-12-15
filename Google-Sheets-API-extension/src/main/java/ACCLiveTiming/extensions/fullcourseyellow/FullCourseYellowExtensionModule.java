/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.fullcourseyellow;

import acclivetiming.ACCLiveTimingExtensionModule;
import acclivetiming.monitor.extensions.AccClientExtension;
import acclivetiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class FullCourseYellowExtensionModule implements ACCLiveTimingExtensionModule{
    
    private FullCourseYellowExtension extension;
    private FullCourseYellowPanel panel;
    
    public FullCourseYellowExtensionModule(){
        extension = new FullCourseYellowExtension();
        panel = new FullCourseYellowPanel(extension);
    }

    @Override
    public String getName() {
        return "Full Course Yellow extension";
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

    @Override
    public LPContainer getExtensionPanel() {
        return panel;
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }
    
}
