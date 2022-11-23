package com.example.project.models.enums;

public enum QuestionDifficulty {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String text;

    QuestionDifficulty(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
