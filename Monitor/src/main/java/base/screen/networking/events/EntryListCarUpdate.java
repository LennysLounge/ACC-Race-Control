/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.data.CarInfo;

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
