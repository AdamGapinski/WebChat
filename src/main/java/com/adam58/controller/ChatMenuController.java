package com.adam58.controller;

import com.adam58.View.ChatMenuView;
import com.adam58.View.IChatMenuView;
import com.adam58.model.Channel;
import com.adam58.model.ChatMenu;
import com.adam58.model.IChatMenu;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam Gapiński
 *
 * ChatMenuController class is reponsible for handling WebSocket events like connect, close
 * and message for chat menu.
 */

@WebSocket
public class ChatMenuController {
    private IChatMenu chatMenu = new ChatMenu();
    private Map<Session, IChatMenuView> views = new ConcurrentHashMap<>();

    /*
    * On web socket connect event, user session is associated with view object, so the user will
    * receive new channels added by other users.
    * */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        IChatMenuView view = new ChatMenuView(session);

        views.put(session, view);
        chatMenu.registerChannelsListener(view);

        view.showChannelsList(chatMenu.getChannelNames());
    }

    /*
    * On web socket close, associated with the given session, view is removed from views and unregistered
    * from channel model channels listeners.
    * */
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        chatMenu.removeChannelsListener(views.get(session));
        views.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(String channelName) {
        chatMenu.addChannel(new Channel(channelName));
    }
}