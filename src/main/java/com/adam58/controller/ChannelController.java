package com.adam58.controller;

import com.adam58.model.Channel;
import com.adam58.model.IChannel;
import com.adam58.model.Message;
import com.adam58.model.User;
import com.adam58.view.ChannelView;
import com.adam58.view.IChannelView;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Adam Gapiński
 *
 * ChannelController class is the implementation of interface designed to handle
 * user interaction in scope of one particular channel.
 */
public class ChannelController implements IChannelController {
    private IChannel channelModel;
    private ScheduledExecutorService userLeftService = Executors.newScheduledThreadPool(1);
    /*
    * usersBySession is mapping sessions to UserModelView, so it is possible to easily obtain all user session-view pairs and
    * model by having only Session object. This allows us to determine whether user is online - has any views.
    * */
    private Map<Session, UserModelView> usersBySession = new ConcurrentHashMap<>();

    /*
    * usersByName map is providing UserModelView objects by username key (String). It is useful when new connection is being made
    * and we have username as a parameter (see handleConnection method).
    * We can check whether the user has any other connections.
    * */
    private Map<String, UserModelView> usersByName = new ConcurrentHashMap<>();

    /*
    * UserModelView class is used to aggregate user views and model object.
    * */
    private class UserModelView {
        private Map<Session, IChannelView> views = new ConcurrentHashMap<>();
        private User model;
        private Future userLeftServiceFuture;

        private UserModelView(User model) {
            this.model = model;
        }

        private IChannelView createView(Session session) {
            IChannelView channelView = new ChannelView(session);
            views.put(session, channelView);
            return channelView;
        }
    }

    ChannelController(String channelName) {
        channelModel = new Channel(channelName);
    }

    @Override
    public void handleConnection(Session session, String username) {
        //Find userModelView by username in usersByName map, if there is no mapping, create and put userModelView object
        UserModelView userModelView = usersByName.computeIfAbsent(username, this::newUserHasJoined);
        usersBySession.put(session, userModelView);

        IChannelView channelView = userModelView.createView(session);

        //Show last 20 messages to the user and update user list
        channelView.showMessages(channelModel.getMessages(20, 0));
        List<String> otherUserNames = channelModel.getUserNames();
        otherUserNames.remove(userModelView.model.getUsername());
        channelView.showUserList(otherUserNames);
        channelView.updateUserList(userModelView.model, true, true);

        //User will receive new messages through the channelView.
        channelModel.registerMessageListener(channelView);
    }

    private UserModelView newUserHasJoined(String username) {
        User model = findUserModel(username);
        channelModel.joinUser(model);
        usersBySession.values()
                .forEach(userModelView -> userModelView.views.values()
                        .forEach(iChannelView -> iChannelView.updateUserList(model, true, false)));
        UserModelView userModelView = new UserModelView(model);

        Message message = new Message("Server",
                String.format("User %s has joined the channel.", username),
                new Date());
        this.broadcastMessage(message);

        return userModelView;
    }

    private User findUserModel(String username) {
        return new User(username);
    }

    @Override
    public void closeConnection(Session session) {
        //When session is closed, the mapping to userModelView is redundant.
        UserModelView userModelView = usersBySession.remove(session);

        //User is closing session, so the corresponding view is removed
        IChannelView view = userModelView.views.remove(session);

        //View will be no longer registered as a message listener in channelModel.
        channelModel.removeMessageListener(view);

        /*
        * If user has no more views, then server schedules userHasLeftChannel method with 3 seconds delay,
        * but it happens only when it has not been already scheduled or if so, then it is rescheduled with
        * new 3 seconds delay.
        * */
        if (userModelView.views.size() == 0 &&
                (userModelView.userLeftServiceFuture == null ||
                        userModelView.userLeftServiceFuture.cancel(false))) {
            userModelView.userLeftServiceFuture = userLeftService.schedule(() -> userHasLeftChannel(userModelView),
                    3, TimeUnit.SECONDS);
        }
    }

    private void userHasLeftChannel(UserModelView modelView) {
        if (modelView.views.size() == 0) {
            User user = modelView.model;
            channelModel.removeUser(user);
            usersBySession.values()
                    .forEach(userModelView -> userModelView.views.values()
                            .forEach(iChannelView -> iChannelView.updateUserList(user, false, false)));
            usersByName.remove(user.getUsername());
            Message message = new Message("Server",
                    String.format("User %s has left the channel.", user.getUsername()),
                    new Date());
            this.broadcastMessage(message);
        }
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
        return !usersByName.isEmpty();
    }

    @Override
    public String getChannelName() {
        return channelModel.getChannelName();
    }
}
