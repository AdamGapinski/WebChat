package com.adam58.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Adam Gapiński
 *
 * Channel class is resonsible for storing data corresponding to particular channel and
 * notifying lsitenerns about changes.
 */
public class Channel implements IChannel {
    private String channelName;
    private List<IMessageListener> messageListeners = new CopyOnWriteArrayList<>();
    private List<Message> messages = new CopyOnWriteArrayList<>();
    private List<User> users = new CopyOnWriteArrayList<>();

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
        messageListeners.forEach(listener -> listener.notifyNewMessage(message));
    }

    @Override
    public List<Message> getMessages(int count, int lastMessageNumToGet) {
        int toIndex = messages.size() - lastMessageNumToGet;
        int fromIndex = toIndex - count;
        return messages.subList(fromIndex < 0 ? 0 : fromIndex, toIndex < 0 ? 0 : toIndex);
    }

    @Override
    public void registerMessageListener(IMessageListener listener) {
        messageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(IMessageListener listener) {
        messageListeners.remove(listener);
    }

    @Override
    public List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();
        users.forEach(user -> userNames.add(user.getUsername()));
        return userNames;
    }

    @Override
    public void joinUser(User user) {
        users.add(user);
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
    }
}

