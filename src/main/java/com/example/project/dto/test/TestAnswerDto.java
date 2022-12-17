package com.example.project.dto.test;

import java.util.List;

public class TestAnswerDto {
    private List<String> answers;
    private List<Integer> matchQuestionNumOfAnswers;
    private List<String> subQuestionsText;

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

    public List<Integer> getMatchQuestionNumOfAnswers() {
        return matchQuestionNumOfAnswers;
    }

    public void setMatchQuestionNumOfAnswers(List<Integer> matchQuestionNumOfAnswers) {
        this.matchQuestionNumOfAnswers = matchQuestionNumOfAnswers;
    }
}
