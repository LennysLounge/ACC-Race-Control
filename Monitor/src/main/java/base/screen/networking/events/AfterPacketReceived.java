/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;

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
