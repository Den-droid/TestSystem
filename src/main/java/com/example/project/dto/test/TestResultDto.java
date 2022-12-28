package com.example.project.dto.test;

import java.util.List;

public class TestResultDto {
    private double mark;
    private String username;
    private String testName;
    private List<TestQuestionDto> questions;
    private List<TestAnswerDto> answers;

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public List<TestQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestionDto> questions) {
        this.questions = questions;
    }

    public List<TestAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<TestAnswerDto> answers) {
        this.answers = answers;
    }
}
