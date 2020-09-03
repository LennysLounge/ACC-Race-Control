/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.extensions.livetiming;

import ACCLiveTiming.networking.enums.DriverCategory;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Leonard
 */
public class ListEntry {

    private final List<String> items;
    private final boolean inPits;
    private final DriverCategory category; 
    
    public ListEntry(List<String> items, boolean inPits, DriverCategory category){
        this.items = items;
        this.inPits = inPits;
        this.category = category;
    }
    

    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public boolean isInPits(){
        return inPits;
    }
    
    public DriverCategory getCategory(){
        return category;
    }
}
