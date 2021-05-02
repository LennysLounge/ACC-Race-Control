/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking;

import base.screen.networking.enums.SessionType;

/**
 *
 * @author Leonard
 */
public class SessionId {

    private SessionType type;

    private int number;

    private int index;

    public SessionId(SessionType type, int index, int number) {
        this.type = type;
        this.index = index;
        this.number = number;
    }

    public SessionType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public int getIndex() {
        return index;
    }

}
