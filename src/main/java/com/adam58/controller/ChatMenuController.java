package com.adam58.controller;

import com.adam58.View.IChatMenuView;
import com.adam58.model.Channel;
import com.adam58.model.IChatMenu;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author Adam Gapiński
 *
 * ChatMenuController class is reponsible for handling WebSocket events like connect, close
 * and message for chat menu.
 */

@WebSocket
public class ChatMenuController {
    private IChatMenu chatMenu;
    private IChatMenuView chatMenuView;

    public ChatMenuController(IChatMenu chatMenu, IChatMenuView chatMenuView) {
        this.chatMenu = chatMenu;
        this.chatMenuView = chatMenuView;

        /*
        * ChatMenuView is registered as listener for changes in channel, therefore it can
        * change presented view for users.
        * */
        this.chatMenu.registerChannelsListener(this.chatMenuView);
    }

    /*
    * On web socket connect, controller add session to view, so session will receive view and any
    * changes made to it.
    * */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        chatMenuView.addSession(session);
        chatMenuView.showChannelsList(chatMenu.getChannelNames(), session);
    }

    /*
    * On web socket close event, controller removes the user session from view, so it will no longer receive
    * changes.
    * */
    @OnWebSocketClose
    public void onClose(Session session) {
        chatMenuView.removeSession(session);
    }

    /*
    * On web socket message event, controller delegates channel creation to ChatMenu model.
    * */
    @OnWebSocketMessage
    public void onMessage(String message) {
        chatMenu.addChannel(new Channel(message));
    }
}