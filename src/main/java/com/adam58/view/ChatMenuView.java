package com.adam58.view;

import com.adam58.model.IChannel;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.article;
import static j2html.TagCreator.p;

/**
 * @author Adam Gapiński
 */
public class ChatMenuView implements IChatMenuView {
    private Session session;

    public ChatMenuView(Session session) {
        this.session = session;
    }

    @Override
    public void receiveChannel(IChannel channel) {
        sendChannelLink(createHtmlLinkToChannel(channel.getChannelName()));
    }
    @Override
    public void showChannelsList(List<String> channelNames) {
        channelNames.forEach((channel) -> sendChannelLink(createHtmlLinkToChannel(channel)));
    }

    private void sendChannelLink(String channelLink) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "channel");
        data.put("content", channelLink);
        sendDataToClient(data);
    }

    private String createHtmlLinkToChannel(String channel) {
        String innerHTML = article().with(p(channel)).render();
        return String.format("<a class=\"list-group-item\" " +
                "href=\"/channel.html?channel=%s\">%s</a>", channel, innerHTML);
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
