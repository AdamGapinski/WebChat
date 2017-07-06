package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChannel {
    String getChannelName();
    void addMessage(Message message);
    List<Message> getMessages(int count, int lastMessageNumToGet);
    void registerMessageListener(IMessageListener listener);
    void removeMessageListener(IMessageListener listener);
    List<String> getUserNames();
    void joinUser(User model);
    void removeUser(User user);
}