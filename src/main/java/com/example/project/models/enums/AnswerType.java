package com.example.project.models.enums;

public enum AnswerType {
    SINGLE("Single"),
    MULTIPLE("Multiple"),
    MATCH("Match"),
    CUSTOM("Custom");

    private final String text;

    AnswerType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
