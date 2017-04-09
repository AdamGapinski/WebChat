package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChatMenu {
    void addChannel(IChannel channel);
    List<String> getChannelNames();
    void registerChannelsListener(IChannelsListener listener);
    void removeChannelsListener(IChannelsListener listener);
}