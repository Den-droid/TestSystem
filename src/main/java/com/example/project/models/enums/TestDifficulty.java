package com.example.project.models.enums;

import java.util.Arrays;

public enum TestDifficulty {
    LOW("Low", 0.2),
    LOW_MEDIUM("Low medium", 0.4),
    MEDIUM("Medium", 0.6),
    LOW_HIGH("Low high", 0.8),
    HIGH("High", 1.0);

    private final String text;

    private final double coefficient;

    TestDifficulty(String text, double coefficient) {
        this.text = text;
        this.coefficient = coefficient;
    }

    public static TestDifficulty getByText(String text) {
        return Arrays.stream(TestDifficulty.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getText() {
        return text;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
