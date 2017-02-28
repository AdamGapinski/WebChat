package com.adam58.View;

import com.adam58.model.IChannelsListener;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChatMenuView extends IChannelsListener {
    boolean addSession(Session session);
    boolean removeSession(Session session);
    void showChannelsList(List<String> channels, Session session);
}