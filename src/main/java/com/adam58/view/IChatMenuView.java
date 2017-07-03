package com.adam58.view;

import com.adam58.model.IChannelsListener;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChatMenuView extends IChannelsListener {
    void showChannelsList(List<String> channels);
}