package com.adam58.controller;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author Adam Gapiński
 */
public class Chatbot {
    String answerQuestion(String questionString) {
        Question question;
        try {
            question = parseQuestion(questionString);
        } catch (Question.QuestionNotDefinedException e) {
            return "I can't understand question.";
        }

        switch (question) {
            case TIME:
                return LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
            case WEEKDAY:
                return capitalize(LocalDate.now().getDayOfWeek().toString());
            case WEATHER:
                return obtainWeather();
        }
        return "";
    }

    private String obtainWeather() {
        StringBuilder weatherDescription = new StringBuilder();

        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=3094802&units=metric&APPID="
                    + Files.readFirstLine(new File("/home/adam/owmapi"), Charset.forName("UTF-8")));
            InputStreamReader reader = new InputStreamReader((InputStream) url.getContent());
            BufferedReader bfReader = new BufferedReader(reader);

            String line;
            if ((line = bfReader.readLine()) != null) {
                JsonElement jsonElement = new JsonParser().parse(line);
                JsonObject jsonObject = jsonElement.getAsJsonObject();


                weatherDescription.append("temperature: ");
                weatherDescription.append(
                        String.valueOf(jsonObject.getAsJsonObject("main")
                                .getAsJsonPrimitive("temp").getAsFloat()))
                        .append(" °C");


                weatherDescription.append(" pressure: ");
                weatherDescription.append(
                        String.valueOf(jsonObject.getAsJsonObject("main")
                                .getAsJsonPrimitive("pressure").getAsFloat()))
                        .append(" hPa");

                weatherDescription.append(" wind: ");
                weatherDescription.append(
                        String.format("%.2f",jsonObject.getAsJsonObject("wind")
                                .getAsJsonPrimitive("speed").getAsFloat() * 3.6f))
                        .append(" km/h");

                weatherDescription.append(" ");
                weatherDescription.append(jsonObject.get("weather")
                        .getAsJsonArray().get(0)
                        .getAsJsonObject().get("description").getAsString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherDescription.toString();
    }

    private String capitalize(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1, word.length()).toLowerCase();
    }

    private Question parseQuestion(String questionString) throws Question.QuestionNotDefinedException {
        return Arrays.stream(Question.values())
                .filter(question -> question.questionPatterns
                        .stream()
                        .anyMatch((pattern) ->
                                pattern.equals(questionString.endsWith("?") ?
                                        questionString.toLowerCase() : questionString.toLowerCase() + "?")))
                .findAny()
                .orElseThrow(Question.QuestionNotDefinedException::new);
    }
}

