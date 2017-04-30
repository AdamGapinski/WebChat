package com.adam58.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

/**
 * @author Adam Gapiński
 */
public class ChannelTest {
    private IChannel channel;
    private List<Message> testMessages;
    @Before
    public void setUp() throws Exception {
        testMessages = Arrays.asList(new Message("hello"),
                new Message("welcome"),
                new Message("hi"),
                new Message("q"),
                new Message("w"),
                new Message("e"),
                new Message("r"),
                new Message("t"),
                new Message("bye"));
        channel = new Channel("");
        testMessages.forEach(message -> channel.addMessage(message));
    }

    @Test
    public void getMessages() throws Exception {
        List<Message> messages = channel.getMessages(5, 0);
        assertTrue(messages.equals(testMessages.subList(4, 9)));

        messages = channel.getMessages(4, 5);
        assertTrue(messages.equals(testMessages.subList(0, 4)));

        messages = channel.getMessages(3, 5);
        assertTrue(messages.equals(testMessages.subList(1, 4)));

        assertThat(channel.getMessages(100, 10).size(), is(0));
        assertThat(channel.getMessages(1, 9).size(), is(0));
        assertThat(channel.getMessages(20, 9).size(), is(0));
        assertThat(channel.getMessages(20, 8).size(), is(1));
        assertThat(channel.getMessages(20, 4).size(), is(5));
    }

}