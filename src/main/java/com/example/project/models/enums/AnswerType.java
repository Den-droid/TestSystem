package com.example.project.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AnswerType {
    SINGLE("Single"),
    MULTIPLE("Multiple"),
    MATCH("Match"),
    CUSTOM("Custom");

    private final String text;

    AnswerType(String text) {
        this.text = text;
    }

    public static AnswerType getByText(String text) {
        return Arrays.stream(AnswerType.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> getValuesText() {
        return Arrays.stream(AnswerType.values())
                .map(AnswerType::getText)
                .collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }
}
