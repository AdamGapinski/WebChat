package com.adam58.View;

import com.adam58.model.IChannel;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        List<String> channelList = new ArrayList<>();
        channelList.add(createHtmlLinkToChannel(channel.getChannelName()));
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", channelList)));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void showChannelsList(List<String> channels) {
        try {
            List<String> channelsJSON = new ArrayList<>();
            channels.forEach(channelName -> channelsJSON.add(createHtmlLinkToChannel(channelName)));

            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", channelsJSON)));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String createHtmlLinkToChannel(String channel) {
        String innerHTML = article().with(p(channel)).render();

        return String.format("<a class=nostyle href=\"/channel.html?channel=%s\">%s</a>", channel, innerHTML);
    }
}
