package com.adam58.controller;


import com.adam58.model.Channel;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author Adam Gapiński
 */

@WebSocket
public class ChatMenuSocketHandler {
    private Chat chat;

    public ChatMenuSocketHandler(Chat chat) {
        this.chat = chat;
        this.chat.getChannels().add(new Channel("Chatbot"));
    }

    @OnWebSocketConnect
    public void onConnect(Session user) {
        chat.getChannelMenuUsers().add(user);
        chat.sendChannelList(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        chat.getChannelMenuUsers().remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        chat.getChannels().add(new Channel(message));
        chat.broadcastChannelList();
    }
}
