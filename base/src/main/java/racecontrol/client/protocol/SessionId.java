/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.protocol;

import racecontrol.client.protocol.enums.SessionType;

/**
 * Serves as a quince identifier for a session with its type, index and which
 * number it is.
 *
 * @author Leonard
 */
public class SessionId {

    private final SessionType type;

    private final int number;

    private final int index;

    /**
     * Creates an uninitialised SessionId. This sessionID is invalid and should
     * be replaces as early as possible by a valid one.
     */
    public SessionId() {
        this(SessionType.NONE, -1, 0);
    }

    /**
     * Creates a session id with the session type, index, and which number it
     * is.
     *
     * @param type The session type.
     * @param index The session index.
     * @param number The count number of this session.
     */
    public SessionId(SessionType type, int index, int number) {
        this.type = type;
        this.index = index;
        this.number = number;
    }

    /**
     * Returns whether or not this sessionID is valid or not.
     * @return True if this sessionID is valid.
     */
    public boolean isValid() {
        return (type != SessionType.NONE || index != -1 || number != 0);
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
