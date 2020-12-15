/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acclivetiming.monitor.networking.enums;

/**
 *
 * @author Leonard
 */
public enum BroadcastingEventType {
    NONE(0),
    GREENFLAG(1),
    SESSIONOVER(2),
    PENALTYCOMMMSG(3),
    ACCIDENT(4),
    LAPCOMPLETED(5),
    BESTSESSIONLAP(6),
    BESTPERSONALLAP(7);

    private int id;

    private BroadcastingEventType(int id) {
        this.id = id;
    }

    public static BroadcastingEventType fromId(int id) {
        switch (id) {
            case 0:
                return NONE;
            case 1:
                return GREENFLAG;
            case 2:
                return SESSIONOVER;
            case 3:
                return PENALTYCOMMMSG;
            case 4:
                return ACCIDENT;
            case 5:
                return LAPCOMPLETED;
            case 6:
                return BESTSESSIONLAP;
            case 7:
                return BESTPERSONALLAP;
            default:
                return NONE;
        }
    }

    public int getId() {
        return id;
    }
}
