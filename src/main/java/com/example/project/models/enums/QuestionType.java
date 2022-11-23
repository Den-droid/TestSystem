package com.example.project.models.enums;

public enum QuestionType {
    TEXT("Text"),
    TEXT_AUDIO("Text with audio"),
    TEXT_PHOTO("Text with photo"),
    TEXT_VIDEO("Text with video");

    private final String text;

    QuestionType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
