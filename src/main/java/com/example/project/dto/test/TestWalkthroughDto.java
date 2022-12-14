package com.example.project.dto.test;

import java.util.List;

public class TestWalkthroughDto {
    private Long questionId;
    private List<Integer> subQuestionNumberOfAnswers;
    private List<String> subQuestionText;
    private List<String> answers;
    private String action;
    private Integer previousNumber;
    private Integer nextNumber;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Integer> getSubQuestionNumberOfAnswers() {
        return subQuestionNumberOfAnswers;
    }

    public void setSubQuestionNumberOfAnswers(List<Integer> subQuestionNumberOfAnswers) {
        this.subQuestionNumberOfAnswers = subQuestionNumberOfAnswers;
    }

    public List<String> getSubQuestionText() {
        return subQuestionText;
    }

    public void setSubQuestionText(List<String> subQuestionText) {
        this.subQuestionText = subQuestionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(Integer previousNumber) {
        this.previousNumber = previousNumber;
    }

    public Integer getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(Integer nextNumber) {
        this.nextNumber = nextNumber;
    }
}
