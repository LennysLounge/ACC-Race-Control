/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.Monitor.client;

import acclivetiming.Monitor.networking.enums.SessionType;

/**
 *
 * @author Leonard
 */
public class SessionId {
    
    private SessionType type;
    
    private int number;
    
    private int index;
    
    public SessionId(SessionType type, int index, int number){
        this.type = type;
        this.index = index;
        this.number = number;
    }

    public SessionType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public int getIndex() {
        return index;
    }
    
    
    
}
