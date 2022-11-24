package com.example.project.models.enums;

import java.util.Arrays;

public enum QuestionType {
    TEXT("Text"),
    TEXT_AUDIO("Text with audio"),
    TEXT_PHOTO("Text with photo"),
    TEXT_VIDEO("Text with video");

    private final String text;

    QuestionType(String text) {
        this.text = text;
    }

    public static QuestionType getByText(String text) {
        return Arrays.stream(QuestionType.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getText() {
        return text;
    }
}
