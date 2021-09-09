/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;

/**
 *
 * @author Leonard
 */
public class AfterPacketReceivedEvent extends Event {

    private byte type;
    private int packageCount;

    public AfterPacketReceivedEvent(byte type, int packageCount) {
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
