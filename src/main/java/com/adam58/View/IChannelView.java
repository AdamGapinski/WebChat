package com.adam58.View;

import com.adam58.model.IMessageListener;
import com.adam58.model.Message;
import com.adam58.model.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChannelView extends IMessageListener {
    void registerUserSession(User user, Session session);
    User unregisterUserSession(Session session);
    void showMessages(List<Message> messages, Session session);
    boolean userHasAnySession(User user);
}