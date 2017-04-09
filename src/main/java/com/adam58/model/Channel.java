package com.adam58.model;

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
        messageListeners.forEach(listener -> listener.receiveMessage(message));
    }

    @Override
    public List<Message> getMessages(int from, int count) {
        // TODO: 03.03.17 Check and avoid sublist index out of bound exception
        return messages;
        /*if (from < 0) {
            from = 0;
        } else if (from >= )
        if (from + count > messages.size()) {
            count = 0;
        }

        return messages.subList(from, from + count);*/
    }

    @Override
    public void registerMessageListener(IMessageListener listener) {
        messageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(IMessageListener listener) {
        messageListeners.remove(listener);
    }
}

