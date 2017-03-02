package com.adam58.controller;

import com.adam58.model.Message;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam Gapiński
 *
 * ChannelSocketController is responsible for delegating websocket events (for example messages) to appropriate channel controllers.
 */

@WebSocket
public class ChannelSocketController {
    private Map<Session, IChannelController> controllersBySession = new ConcurrentHashMap<>();
    private Map<String, IChannelController> controllersByName = new ConcurrentHashMap<>();

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        IChannelController controller = controllersBySession.remove(session);
        controller.closeConnection(session);

        removeIfRedundant(controller);
    }

    private void removeIfRedundant(IChannelController controller) {
        /*
        * If the channel controller does not have any more connections, then there is no need to
        * keep it' s reference in controllersByName map.
        * The reference is removed from controllersBySession in each call of onClose method.
        * */
        if (!controller.hasAnyConnections()) {
            controllersByName.remove(controller.getChannelName());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        switch (getValueFromStringJson(message, "type")) {
            case "message":
                broadcastMessage(user, message);
                break;
            case "connection":
                //This type of message means that, a new connection to webSocket has been made.
                handleConnection(user, message);
                break;
        }
    }

    private void broadcastMessage(Session user, String message) {
        String sender = getValueFromStringJson(message, "username");
        String content = getValueFromStringJson(message, "content");

        controllersBySession.get(user).broadcastMessage(new Message(sender, content));
    }

    private void handleConnection(Session session, String message) {
        String channelName = getValueFromStringJson(message, "channel");
        String username = getValueFromStringJson(message, "username");

        /*
        * Look for channel by given name in map, if not present create new controller passing channel name.
        * */
        IChannelController channelController = controllersByName
                .computeIfAbsent(channelName, ChannelController::new);

        channelController.handleConnection(session, username);
        controllersBySession.put(session, channelController);
    }

    private String getValueFromStringJson(String json, String varName) {
        String result = "";

        try {
            JSONObject jsonMessage = new JSONObject(json);
            result = jsonMessage.getString(varName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
