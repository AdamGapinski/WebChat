package com.adam58.controller;

import com.adam58.model.Channel;
import com.adam58.model.User;
import j2html.tags.ContainerTag;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static j2html.TagCreator.*;

/**
 * @author Adam Gapiński
 */
public class Chat {
    private List<Channel> channels = new CopyOnWriteArrayList<>();
    private List<Session> channelMenuUsers = new CopyOnWriteArrayList<>();
    private Map<Session, Channel> userChannelMap = new ConcurrentHashMap<>();

    public void broadcastChannelList() {
        channelMenuUsers.stream()
                .filter(Session::isOpen)
                .forEach(this::sendChannelList);
    }

    public void sendChannelList(Session user) {
        try {
            List<String> channelsJSON = new ArrayList<>();
            channels.forEach(ch -> channelsJSON.add(createHtmlLinkToChannel(ch.getName())));

            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", channelsJSON)));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessageToUserChannel(Session user, String sender, String message) {
        Channel channel = userChannelMap.get(user);

        List<User> users = channel.getUsers();
        List<String> userNames = new ArrayList<>();

        users.forEach(u -> userNames.add(u.username));

        users.stream()
                .filter(u -> u.session.isOpen())
                .forEach(u -> {
                    try {
                        u.session.getRemote().sendString(
                                String.valueOf(new JSONObject()
                                .put("message", createHtmlMessageFromSender(sender, message))
                                .put("userlist", userNames)));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });

        if (channel.getName().toLowerCase().equals("chatbot") &&
                !sender.toLowerCase().equals("chatbot") &&
                !sender.toLowerCase().equals("server")) {
            Chatbot chatbot = new Chatbot();
            broadcastMessageToUserChannel(user, "Chatbot", chatbot.answerQuestion(message));
        }
    }

    private String createHtmlMessageFromSender(String sender, String message) {
        ContainerTag result = article();

        if (sender.toLowerCase().equals("server")) {
            result.withId("serversender");
        }

        return result.with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

    private String createHtmlLinkToChannel(String channel) {
        String innerHTML = article().with(p(channel)).render();

        return String.format("<a class=nostyle href=\"/channel.html?channel=%s\">%s</a>", channel, innerHTML);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Session> getChannelMenuUsers() {
        return channelMenuUsers;
    }

    public Map<Session, Channel> getUserChannelMap() {
        return userChannelMap;
    }
}
