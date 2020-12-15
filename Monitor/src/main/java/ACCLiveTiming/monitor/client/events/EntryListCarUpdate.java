/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.client.events;

import acclivetiming.monitor.eventbus.Event;
import acclivetiming.monitor.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class EntryListCarUpdate extends Event {

    private CarInfo info;

    public EntryListCarUpdate(CarInfo info) {
        this.info = info;
    }

    public CarInfo getSessionInfo() {
        return info;
    }

}
