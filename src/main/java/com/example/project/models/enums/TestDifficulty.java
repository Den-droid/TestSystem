package com.example.project.models.enums;

import java.util.Arrays;

public enum TestDifficulty {
    LOW("Low"),
    LOW_MEDIUM("Low medium"),
    MEDIUM("Medium"),
    LOW_HIGH("Low high"),
    HIGH("High");

    private final String text;

    TestDifficulty(String text) {
        this.text = text;
    }

    public static TestDifficulty getByText(String text) {
        return Arrays.stream(TestDifficulty.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getText() {
        return text;
    }
}
