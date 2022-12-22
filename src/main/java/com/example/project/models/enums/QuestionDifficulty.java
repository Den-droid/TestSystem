package com.example.project.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionDifficulty {
    LOW("Low", 0.33),
    MEDIUM("Medium", 0.66),
    HIGH("High", 1.0);

    private final String text;
    private final double coefficient;

    QuestionDifficulty(String text, double coefficient) {
        this.text = text;
        this.coefficient = coefficient;
    }

    public static QuestionDifficulty getByText(String text) {
        return Arrays.stream(QuestionDifficulty.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> getValuesText() {
        return Arrays.stream(QuestionDifficulty.values())
                .map(QuestionDifficulty::getText)
                .collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
