package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChatMenu {
    boolean addNewChannelName(String channelName);
    List<String> getChannelNames();
    void registerChannelsListener(IChannelsListener listener);
    void removeChannelsListener(IChannelsListener listener);
}