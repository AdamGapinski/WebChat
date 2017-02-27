package com.adam58.controller;

import spark.Spark;

/**
 * @author Adam Gapiński
 *
 * Main class serves as entry point for the whole application. Main mthod initializes Spark framework and set
 * up web application which is accessible at localhost:4567
 */
public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();

        /*
        * webSocket method registers java class to handle javascript web socket requests. Web socket
        * is accessible at given url path
        * */
        Spark.webSocket("/index", new ChatMenuSocketHandler(chat));
        Spark.webSocket("/channel", new ChannelSocketHandler(chat));
        setStaticFileLocation();

        Spark.init();
    }

    private static void setStaticFileLocation() {
        String projectDir;
        if ((projectDir = System.getProperty("user.dir")).contains("IdeaProjects")) {
            String staticDir = "/src/main/resources/public";
            Spark.externalStaticFileLocation(projectDir + staticDir);
        } else {
            Spark.staticFileLocation("/public");
        }
    }
}
