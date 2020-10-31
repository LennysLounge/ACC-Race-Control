/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.networking.enums;

/**
 *
 * @author Leonard
 */
public enum SessionPhase {

    NONE(0),
    STARTING(1),
    PREFORMATION(2),
    FORMATIONLAP(3),
    PRESESSION(4),
    SESSION(5),
    SESSIONOVER(6),
    POSTSESSION(7),
    RESULTUI(8);

    private int id;

    private SessionPhase(int id) {
        this.id = id;
    }

    public static SessionPhase fromId(int id) {
        switch (id) {
            case 0:
                return NONE;
            case 1:
                return STARTING;
            case 2:
                return PREFORMATION;
            case 3:
                return FORMATIONLAP;
            case 4:
                return PRESESSION;
            case 5:
                return SESSION;
            case 6:
                return SESSIONOVER;
            case 7:
                return POSTSESSION;
            case 8:
                return RESULTUI;
            default:
                return NONE;
        }
    }

    public int getId() {
        return id;
    }

    public static SessionPhase getNext(SessionPhase phase) {
        if (phase == RESULTUI) {
            return phase;
        }
        return SessionPhase.values()[phase.ordinal() + 1];
    }
}
