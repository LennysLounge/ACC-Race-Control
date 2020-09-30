/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.visualisation.gui.LPTable;
import java.util.function.Function;

/**
 *
 * @author Leonard
 */
public class LiveTimingEntry extends LPTable.Entry {

    private String one;
    public static Function<LiveTimingEntry, String> oneContent = (e) -> e.getOne();

    private int two;
    public static Function<LiveTimingEntry, String> twoContent = (e) -> e.getTwo();

    private float three;
    public static Function<LiveTimingEntry, String> threeContent = (e) -> e.getThree();
    
    public LiveTimingEntry(String one, int two, float three){
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public void setOne(String one){
        this.one = one;
    }
    
    public String getOne() {
        return one;
    }

    public void setTwo(int two){
        this.two = two;
    }
    
    public String getTwo() {
        return String.valueOf(two);
    }

    public void setThree(float three){
        this.three = three;
    }
    
    public String getThree() {
        return String.valueOf(three);
    }

}
