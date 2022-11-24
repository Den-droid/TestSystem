package com.example.project.models.enums;

import java.util.Arrays;

public enum QuestionDifficulty {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String text;

    QuestionDifficulty(String text) {
        this.text = text;
    }

    public static QuestionDifficulty getByText(String text) {
        return Arrays.stream(QuestionDifficulty.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getText() {
        return text;
    }
}
