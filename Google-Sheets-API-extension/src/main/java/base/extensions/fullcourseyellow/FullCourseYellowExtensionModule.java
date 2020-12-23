/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.extensions.fullcourseyellow;

import base.screen.extensions.AccClientExtension;
import javax.swing.JPanel;
import base.ACCLiveTimingExtensionFactory;

/**
 *
 * @author Leonard
 */
public class FullCourseYellowExtensionModule implements ACCLiveTimingExtensionFactory{

    @Override
    public String getName() {
        return "Full Course Yellow extension";
    }

    @Override
    public AccClientExtension createExtension() {
        return new FullCourseYellowExtension();
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        return null;
    }
    
}
