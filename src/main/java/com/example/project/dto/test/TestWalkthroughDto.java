package com.example.project.dto.test;

import java.util.List;

public class TestWalkthroughDto {
    private Long questionId;
    private String answerType;
    private List<String> subQuestionsText;
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

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public List<String> getSubQuestionsText() {
        return subQuestionsText;
    }

    public void setSubQuestionsText(List<String> subQuestionsText) {
        this.subQuestionsText = subQuestionsText;
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
