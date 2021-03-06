package com.adam58.view;

import com.adam58.model.Message;
import com.adam58.model.User;
import j2html.tags.ContainerTag;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

/**
 * @author Adam Gapiński
 *
 * ChannelView class is responsible for presenting channel data (like messages) to users. Using session interface
 * it sends html, which then is inserted to page using javascript.
 */
public class ChannelView implements IChannelView {
    private Session session;

    public ChannelView(Session session) {
        this.session = session;
    }

    @Override
    public void notifyNewMessage(Message message) {
        sendMessageToClient(message);
    }

    @Override
    public void showMessages(List<Message> messages) {
        messages.forEach(this::sendMessageToClient);
    }

    private void sendMessageToClient(Message message) {
        Map<String, String> messageData = new HashMap<>();
        messageData.put("type", "message");
        messageData.put("message", createHtmlMessage(message));
        sendDataToClient(messageData);
    }

    private String createHtmlMessage(Message message) {
        ContainerTag result = article();
        String messageClass = message.getSender().toLowerCase().equals("server") ?
                "list-group-item list-group-item-info" : "list-group-item";
        return result.with(
                b(message.getSender() + " says"),
                p(message.getContent()),
                span().withText(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(message.getDatetime()))
                        .withClass("time")
        ).withClass(messageClass).render();
    }

    @Override
    public void updateUserList(User user, boolean joined, boolean currentUser) {
        Map<String, String> userData = new HashMap<>();
        userData.put("type", joined ? "userJoined" : "userLeft");
        userData.put("username", user.getUsername());
        userData.put("currentUser", String.valueOf(currentUser));
        sendDataToClient(userData);
    }

    @Override
    public void showUserList(List<String> usersNames) {
        usersNames.forEach(username -> {
            Map<String, String> userData = new HashMap<>();
            userData.put("type", "userJoined");
            userData.put("username", username);
            userData.put("currentUser", "false");
            sendDataToClient(userData);
        });
    }

    private void sendDataToClient(Map<String, String> data) {
        JSONObject jsonObject = new JSONObject();
        data.forEach((key, value) -> {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        try {
            if (session.isOpen() && !data.isEmpty()) {
                session.getRemote().sendString(String.valueOf(jsonObject));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
