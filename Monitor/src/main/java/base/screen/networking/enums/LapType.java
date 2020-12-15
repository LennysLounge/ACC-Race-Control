/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.networking.enums;

/**
 *
 * @author Leonard
 */
public enum LapType {

    ERROR(0),
    OUTLAP(1),
    REGULAR(2),
    INLAP(3);

    private int id;

    private LapType(int id) {
        this.id = id;
    }

    public static LapType fromId(int id) {
        switch (id) {
            case 0:
                return ERROR;
            case 1:
                return OUTLAP;
            case 2:
                return REGULAR;
            case 3:
                return INLAP;
            default:
                return ERROR;
        }
    }

    public int getId() {
        return id;
    }

}
