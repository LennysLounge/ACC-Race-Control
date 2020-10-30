/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.networking.enums;

/**
 *
 * @author Leonard
 */
public enum SessionType {

    PRACTICE(0),
    QUALIFYING(4),
    SUPERPOLE(9),
    RACE(10),
    HOTLAP(11),
    HOTSTINT(12),
    HOTLAPSUPERPOLE(13),
    REPLAY(14),
    NONE(255);

    private int id;

    private SessionType(int id) {
        this.id = id;
    }

    public static SessionType fromId(int id) {
        switch (id) {
            case 0:
                return PRACTICE;
            case 4:
                return QUALIFYING;
            case 9:
                return SUPERPOLE;
            case 10:
                return RACE;
            case 11:
                return HOTLAP;
            case 12:
                return HOTSTINT;
            case 13:
                return HOTLAPSUPERPOLE;
            case 14:
                return REPLAY;
            default:
                return NONE;
        }
    }

    public int getId() {
        return id;
    }

}
