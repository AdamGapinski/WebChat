package com.adam58.controller;


import static spark.Spark.*;

/**
 * @author Adam Gapiński
 */
public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();
        webSocket("/index", new ChatMenuSocketHandler(chat));
        webSocket("/channel", new ChannelSocketHandler(chat));
        setStaticFileLocation();

        init();
    }

    private static void setStaticFileLocation() {
        String projectDir;
        if ((projectDir = System.getProperty("user.dir")).contains("IdeaProjects")) {
            String staticDir = "/src/main/resources/public";
            externalStaticFileLocation(projectDir + staticDir);
        } else {
            staticFileLocation("/public");
        }
    }
}
