/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions;

import processing.core.PGraphics;

/**
 *
 * @author Leonard
 */
@FunctionalInterface
public interface GraphicsFactory {
    
    PGraphics createGraphics(int w, int h);
    
}
