package com.adam58.view;

import com.adam58.model.Message;
import j2html.tags.ContainerTag;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void receiveMessage(Message message) {
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("message", createHtmlMessageFromSender(message.getSender(), message.getContent()))
                    .put("userlist", new ArrayList<>())// TODO: 03.03.17 user list
            ));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessages(List<Message> messages) {
        messages.forEach(message -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                                .put("message", createHtmlMessageFromSender(message.getSender(), message.getContent()))
                                .put("userlist", new ArrayList<>())
                        ));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private String createHtmlMessageFromSender(String sender, String message) {
        ContainerTag result = article();

        if (sender.toLowerCase().equals("server")) {
            result.withId("serversender");
        }

        // TODO: 03.03.17 Message timestamp should be get from the message object
        return result.with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
}
