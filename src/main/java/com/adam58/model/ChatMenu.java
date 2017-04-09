package com.adam58.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Adam Gapiński
 */
public class ChatMenu implements IChatMenu {
    private List<IChannel> channels = new CopyOnWriteArrayList<>();
    private List<IChannelsListener> channelsListeners = new CopyOnWriteArrayList<>();

    @Override
    public void addChannel(IChannel channel) {
        channels.add(channel);
        notifyAboutNewChannel(channel);
    }

    @Override
    public List<String> getChannelNames() {
        List<String> result = new ArrayList<>();
        channels.forEach(iChannel -> result.add(iChannel.getChannelName()));

        return result;
    }

    @Override
    public void registerChannelsListener(IChannelsListener listener) {
        channelsListeners.add(listener);
    }

    @Override
    public void removeChannelsListener(IChannelsListener listener) {
        channelsListeners.remove(listener);
    }

    private void notifyAboutNewChannel(IChannel channel) {
        channelsListeners.forEach(iChannelsListener -> {
            iChannelsListener.receiveChannel(channel);
        });
    }
}
