package com.adam58.controller;


import com.adam58.model.Channel;
import com.adam58.model.User;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Adam Gapiński
 */

@WebSocket
public class ChannelSocketHandler {
    private Chat chat;

    public ChannelSocketHandler(Chat chat) {
        this.chat = chat;
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        Channel channel = chat.getUserChannelMap().get(user);
        User removedUser = channel.removeUser(user);

        chat.broadcastMessageToUserChannel(user, "Server",
                String.format("%s has left the channel.", removedUser.username));

        chat.getUserChannelMap().remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

        try {
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.getString("type");

            String username = username = jsonMessage.getString("username");;
            switch (type) {
                case "message":
                    String text = jsonMessage.getString("text");
                    chat.broadcastMessageToUserChannel(user, username, text);
                    break;

                case "channeluser" :
                    String channel = jsonMessage.getString("channel");
                    addToChannel(user, username, channel);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addToChannel(Session user, String username, String channel) {
        chat.getChannels().stream()
                .filter(c -> c.getName().equals(channel))
                .findAny()
                .ifPresent(ch -> {
                    ch.addUser(user, username);
                    chat.getUserChannelMap().put(user, ch);
                    chat.broadcastMessageToUserChannel(user, "Server",
                            String.format("%s has joined the channel.", username));
                });
    }
}
