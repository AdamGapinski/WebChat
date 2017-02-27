package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChannel {
    String getChannelName();

    boolean addUser(User user);
    boolean removeUser(User user);
    List<String> getUserNames();

    boolean addMessage(Message message);
    void registerMessageListener(IMessageListener listener);
    void removeMessageListener(IMessageListener listener);
}