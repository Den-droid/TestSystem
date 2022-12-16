package com.example.project.dto.test;

import java.util.List;

public class TestAnswerDto {
    private List<String> answers;
    private List<String> subQuestionsText;
    private int numberOfPreviousAnswers;

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getSubQuestionsText() {
        return subQuestionsText;
    }

    public void setSubQuestionsText(List<String> subQuestionsText) {
        this.subQuestionsText = subQuestionsText;
    }

    public int getNumberOfPreviousAnswers() {
        return numberOfPreviousAnswers;
    }

    public void setNumberOfPreviousAnswers(int numberOfPreviousAnswers) {
        this.numberOfPreviousAnswers = numberOfPreviousAnswers;
    }
}
