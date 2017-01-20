package com.adam58.model;

import org.eclipse.jetty.websocket.api.Session;

/**
 * @author Adam Gapiński
 */
public class User {
    public final String username;
    public final Session session;

    public User(Session session, String username) {
        this.username = username;
        this.session = session;
    }
}
