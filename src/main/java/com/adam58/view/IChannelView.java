package com.adam58.view;

import com.adam58.model.IMessageListener;
import com.adam58.model.Message;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IChannelView extends IMessageListener {
    void showMessages(List<Message> messages);
}