package com.adam58.controller;

import com.adam58.View.ChannelView;
import com.adam58.View.IChannelView;
import com.adam58.model.Channel;
import com.adam58.model.IChannel;
import com.adam58.model.Message;
import com.adam58.model.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam Gapiński
 *
 * ChannelController class is the implementation of interface designed to handle
 * user interaction in scope of one particular channel.
 */
public class ChannelController implements IChannelController {
    private IChannel channelModel;

    /*
    * userModelViewMap is mapping sessions to UserModelView, so it is possible to easily obtain all user session-view pairs and
    * model by having only Session object. This allows us to determine whether user is online - has any views.
    * */
    private Map<Session, UserModelView> userModelViewMap = new ConcurrentHashMap<>();

    /*
    * users map is providing UserModelView objects by username key (String). It is useful when new connection is being made
    * and we have username as a parameter (see handleConnection method).
    * We can check whether the user has any other connections.
    * */
    private Map<String, UserModelView> users = new ConcurrentHashMap<>();

    /*
    * UserModelView class is used to aggregate user views and model object.
    * */
    private class UserModelView {
        private Map<Session, IChannelView> views = new ConcurrentHashMap<>();
        private User model;

        private UserModelView(User model) {
            this.model = model;
        }

        private IChannelView createView(Session session) {
            IChannelView channelView = new ChannelView(session);
            views.put(session, channelView);
            return channelView;
        }
    }

    public ChannelController(String channelName) {
        channelModel = new Channel(channelName);
    }

    @Override
    public void handleConnection(Session session, String username) {
        //Find userModelView by username in users map, if there is no mapping, create and put userModelView object
        UserModelView userModelView = users.computeIfAbsent(username, this::newUserHasJoined);
        userModelViewMap.put(session, userModelView);

        IChannelView channelView = userModelView.createView(session);

        //Show last 20 messages to the user.
        channelView.showMessages(channelModel.getMessages(20, 0));

        //User will receive new messages through the channelView.
        channelModel.registerMessageListener(channelView);
    }

    private UserModelView newUserHasJoined(String username) {
        User model = findUserModel(username);
        UserModelView userModelView = new UserModelView(model);

        this.broadcastMessage(new Message("Server",
                String.format("User %s has joined the channel.", username)));

        return userModelView;
    }

    private User findUserModel(String username) {
        return new User(username);
    }

    @Override
    public void closeConnection(Session session) {
        //When session is closed, the mapping to userModelView is redundant.
        UserModelView userModelView = userModelViewMap.remove(session);

        //User is closing session, so the corresponding view is removed
        IChannelView view = userModelView.views.remove(session);

        //View will be no longer registered as a message listener in channelModel.
        channelModel.removeMessageListener(view);

        if (userModelView.views.size() == 0) {
            userHasLeftTheChannel(userModelView.model);
        }
    }

    private void userHasLeftTheChannel(User user) {
        users.remove(user.getUsername());
        this.broadcastMessage(new Message("Server",
                String.format("User %s has left the channel.", user.getUsername())));
    }

    @Override
    public void broadcastMessage(Message message) {
        /*
        * channelView is registered as a listener for messages in channelModel, so it will be notified
        * about new messages in channelModel
        * */
        channelModel.addMessage(message);
    }

    @Override
    public boolean hasAnyConnections() {
        return !users.isEmpty();
    }

    @Override
    public String getChannelName() {
        return channelModel.getChannelName();
    }
}
