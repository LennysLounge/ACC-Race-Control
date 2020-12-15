/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.client.events;

import acclivetiming.monitor.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class AfterPacketReceived extends Event {

    private byte type;
    private int packageCount;

    public AfterPacketReceived(byte type, int packageCount) {
        this.type = type;
        this.packageCount = packageCount;
    }

    public byte getType() {
        return type;
    }

    public int getPackageCount() {
        return packageCount;
    }

}
