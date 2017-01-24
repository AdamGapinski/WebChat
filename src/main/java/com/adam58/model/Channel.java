package com.adam58.model;

import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Adam Gapiński
 */
public class Channel {
    private final String name;
    private final List<User> users = new CopyOnWriteArrayList<>();

    public Channel(String name) {
        if (name == null || name.equals("")) {
            this.name = "unnamed";
        } else {
            this.name = name;
        }
    }

    public void addUser(Session user, String username) {
        users.add(new User(user, username));
    }

    public User removeUser(Session user) {
        User userToRemove = users.stream()
                .filter(u -> u.session.equals(user))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("User session not found."));

        users.remove(userToRemove);
        return userToRemove;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return new CopyOnWriteArrayList<>(users);
    }
}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

