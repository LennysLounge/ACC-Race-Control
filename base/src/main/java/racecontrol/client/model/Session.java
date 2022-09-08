/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.model;

import racecontrol.client.protocol.SessionInfo;

/**
 * Represents a session.
 *
 * @author Leonard
 */
public class Session {

    /**
     * Raw session info object.
     */
    public SessionInfo raw = new SessionInfo();
    

    public Session copy() {
        Session session = new Session();
        session.raw = raw;
        return session;
    }
}
