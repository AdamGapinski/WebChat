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
import static spark.Spark.*;

/**
 * @author Adam Gapiński
 */
public class Chat {
    public final static List<Channel> channels = new CopyOnWriteArrayList<>();
    public final static List<Session> channelMenuUsers = new CopyOnWriteArrayList<>();
    public final static Map<Session, Channel> userChannelMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        webSocket("/index", ChatMenuSocketHandler.class);
        webSocket("/channel", ChannelSocketHandler.class);
        setStaticFileLocation();
        channels.add(new Channel("Chatbot"));

        init();
    }
    private static void setStaticFileLocation() {
        String projectDir;
        if ((projectDir = System.getProperty("user.dir")).contains("IdeaProjects")) {
            String staticDir = "/src/main/resources/public";
            externalStaticFileLocation(projectDir + staticDir);
        } else {
            staticFileLocation("/public");
        }
    }

    public static void broadcastChannelList(String newChannel) {
        if (newChannel != null) {
            channels.add(new Channel(newChannel));
        }
        channelMenuUsers.stream()
                .filter(Session::isOpen)
                .forEach(Chat::sendChannelList);
    }

    public static void sendChannelList(Session user) {
        try {
            List<String> channelsJSON = new ArrayList<>();
            channels.forEach(ch -> channelsJSON.add(createHtmlLinkToChannel(ch.getName())));

            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", channelsJSON)
                    .put("type", "channels")));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessageToUserChannel(Session user, String sender, String message) {
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

    private static String createHtmlMessageFromSender(String sender, String message) {
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

    private static String createHtmlLinkToChannel(String channel) {
        String innerHTML = article().with(p(channel)).render();

        return String.format("<a class=nostyle href=\"/channel.html?channel=%s\">%s</a>", channel, innerHTML);
    }
}
