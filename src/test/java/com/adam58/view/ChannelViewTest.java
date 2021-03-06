package com.adam58.view;

import com.adam58.model.Message;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

/**
 * @author Adam Gapiński
 */
public class ChannelViewTest {
    private IChannelView view;
    private RemoteEndpoint remote;

    @Captor
    private ArgumentCaptor<String> captor;

    @Before
    public void setUp() throws Exception {
        remote = mock(RemoteEndpoint.class);
        Session mockSession = mock(Session.class);
        when(mockSession.getRemote()).thenReturn(remote);
        when(mockSession.isOpen()).thenReturn(true);
        view = new ChannelView(mockSession);
        captor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void receiveMessage() throws Exception {
        Message message = new Message("adam", "welcome", new Date());
        view.notifyNewMessage(message);
        verify(remote, times(1)).sendString(captor.capture());

        String sent = captor.getValue();
        assertThat(sent, allOf(containsString("{"),
                containsString("}"),
                containsString("\"type\":\"message\""),
                containsString("\"message\":"),
                containsString("<article"),
                containsString("list-group-item"),
                containsString("time"),
                containsString("adam"),
                containsString("welcome")));
    }

    @Test
    public void showMessages() throws Exception {
        List<Message> messages  = Arrays.asList(new Message("adam", "hello", new Date()),
                new Message("tom", "hi", new Date()),
                new Message("ad", "te", new Date()));
        view.showMessages(messages);
        verify(remote, times(messages.size())).sendString(captor.capture());

        List<String> sentValues = captor.getAllValues();
        for (int i = 0; i < messages.size(); i++) {
            assertThat(sentValues.get(i), allOf(containsString("{"),
                    containsString("}"),
                    containsString("\"type\":\"message\""),
                    containsString("\"message\":"),
                    containsString("<article"),
                    containsString("list-group-item"),
                    containsString("time"),
                    containsString(messages.get(i).getSender()),
                    containsString(messages.get(i).getContent())));
        }
    }
}