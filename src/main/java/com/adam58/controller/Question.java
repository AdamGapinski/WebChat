package com.adam58.controller;

import java.util.Arrays;
import java.util.List;

/**
 * @author Adam Gapiński
 */
enum Question {
    TIME(Arrays.asList("What time is it?", "time")),
    WEEKDAY(Arrays.asList("What is the day of week?", "day of week")),
    WEATHER(Arrays.asList("How is the weather?", "What is the weather like?", "weather"));

    final List<String> questionPatterns;

    Question(List<String> question) {
        this.questionPatterns = question;
    }

    static class QuestionNotDefinedException extends Exception {
    }
}
