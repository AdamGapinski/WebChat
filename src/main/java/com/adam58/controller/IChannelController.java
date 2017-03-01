package com.adam58.controller;

import com.adam58.model.Message;
import org.eclipse.jetty.websocket.api.Session;

/**
 * @author Adam Gapiński
 */
public interface IChannelController {
    String getChannelName();
    void handleConnection(Session session, String username);
    void closeConnection(Session session);
    boolean hasAnyConnections();
    void broadcastMessage(Message message);
}
