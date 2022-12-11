package com.example.project.models.enums;

import java.util.Arrays;

public enum TestType {
    ALL("All"),
    CURRENT("Current"),
    FINISHED("Finished");

    private final String text;

    TestType(String text) {
        this.text = text;
    }

    public static TestType getByText(String text) {
        return Arrays.stream(TestType.values())
                .filter(x -> x.getText().equals(text))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getText() {
        return text;
    }
}
