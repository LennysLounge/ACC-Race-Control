/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.testomato;

import ACCLiveTiming.extensions.AccClientExtension;

/**
 *
 * @author Leonard
 */
public class TestomatoExtension extends AccClientExtension {
    
    public TestomatoExtension() {
        this.panel = new TestomatoPanel();
    }
    
}
