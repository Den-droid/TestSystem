package com.example.project.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TestType {
    CREATED("Created"),
    CURRENT("Current"),
    FINISHED("Finished"),
    ASSIGNED("Assigned");

    private final String text;

    TestType(String text) {
        this.text = text;
    }

    public static TestType getByText(String text) {
        return Arrays.stream(TestType.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> getValuesText() {
        return Arrays.stream(TestType.values())
                .map(TestType::getText)
                .collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }
}
