package com.adam58.controller;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Adam Gapiński
 */

@WebSocket
public class ChatMenuSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) {
        Chat.channelMenuUsers.add(user);
        Chat.sendChannelList(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        Chat.channelMenuUsers.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if (message.startsWith("username:")) {

            List<String> usernames = new ArrayList<>();
            Chat.channels.forEach(
                    channel -> channel.getUsers().forEach((u) -> usernames.add(u.username))
            );

            user.getRemote().sendString(
                    String.valueOf(
                            new JSONObject()
                                    .put("type", "usernames")
                                    .put("usernames", Chat.channels)
                    )
            );
        }
        else {
            Chat.broadcastChannelList(message);
        }
    }
}
