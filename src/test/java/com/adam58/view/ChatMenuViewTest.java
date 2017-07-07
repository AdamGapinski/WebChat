package com.adam58.view;

import com.adam58.model.Channel;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Adam Gapiński
 */
public class ChatMenuViewTest {
    private IChatMenuView view;
    private RemoteEndpoint remote;

    @Captor
    private ArgumentCaptor<String> captor;

    @Before
    public void setUp() throws Exception {
        remote = mock(RemoteEndpoint.class);
        Session mockSession = mock(Session.class);
        when(mockSession.getRemote()).thenReturn(remote);
        when(mockSession.isOpen()).thenReturn(true);
        view = new ChatMenuView(mockSession);
        captor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void receiveChannel() throws Exception {
        List<String> channelNames = Arrays.asList("Test", "Sport", "Programming");

        for (int channelIndex = 0; channelIndex < channelNames.size(); ++channelIndex) {
            String channelName = channelNames.get(channelIndex);
            view.receiveChannel(new Channel(channelName));

            verify(remote, times(channelIndex + 1)).sendString(captor.capture());
            String sent = captor.getValue();

            assertThat(sent, allOf(containsString("{"),
                    containsString("}"),
                    containsString("href="),
                    containsString("/channel.html?"),
                    containsString(String.format("channel=%s", channelName))));
        }
    }

    @Test
    public void showChannelsList() throws Exception {
        List<String> channelNames = Arrays.asList("Test", "Sport", "Programming");
        view.showChannelsList(channelNames);

        verify(remote, times(3)).sendString(captor.capture());
        List<String> sentChannels = captor.getAllValues();
        sentChannels.forEach(sent -> {
            assertThat(sent, allOf(containsString("{"),
                    containsString("}"),
                    containsString("href="),
                    containsString("/channel.html?")));
            assertThat(sent, anyOf(containsString("Test"),
                    containsString("Sport"),
                    containsString("Programming")));
        });
    }
}
