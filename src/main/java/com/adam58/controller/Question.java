package com.adam58.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam Gapiński
 */
enum Question {
    TIME(Arrays.asList("która godzina?", "ktora godzina?")),
    WEEKDAY(Arrays.asList("jaki dziś dzień tygodnia?", "jaki dzis dzien tygodnia?")),
    WEATHER(Collections.singletonList("jaka jest pogoda w krakowie?"));

    final List<String> questionPatterns;

    Question(List<String> question) {
        this.questionPatterns = question;
    }

    static class QuestionNotDefinedException extends Exception {
    }
}
