/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.data;

import base.screen.networking.enums.BroadcastingEventType;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Leonard
 */
public class BroadcastingEvent {

    private BroadcastingEventType Type;
    private String message = "";
    private int timeMs;
    private int carId;

    public static int TYPE_NONE = 0;
    public static int TYPE_GREENFLAG = 1;
    public static int TYPE_SESSIONOVER = 2;
    public static int TYPE_PENALTYCOMMMSG = 3;
    public static int TYPE_ACCIDENT = 4;
    public static int TYPE_LAPCOMPLETED = 5;
    public static int TYPE_BESTSESSIONLAP = 6;
    public static int TYPE_BESTPERSONALLAP = 7;

    public BroadcastingEvent() {
    }

    public BroadcastingEvent(BroadcastingEventType Type, String message, int timeMs, int carId) {
        this.Type = Type;
        this.message = requireNonNull(message, "message");
        this.timeMs = timeMs;
        this.carId = carId;
    }

    public BroadcastingEventType getType() {
        return Type;
    }

    public String getMessage() {
        return message;
    }

    public int getTimeMs() {
        return timeMs;
    }

    public int getCarId() {
        return carId;
    }

}
