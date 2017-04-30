package com.adam58.View;

import com.adam58.model.IChannel;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", Collections.singleton(createHtmlLinkToChannel(channel.getChannelName())))));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void showChannelsList(List<String> channels) {
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channels", channels.stream()
                            .map(this::createHtmlLinkToChannel)
                            .collect(Collectors.toList()))));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String createHtmlLinkToChannel(String channel) {
        String innerHTML = article().with(p(channel)).render();

        return String.format("<a class=nostyle href=\"/channel.html?channel=%s\">%s</a>", channel, innerHTML);
    }
}
