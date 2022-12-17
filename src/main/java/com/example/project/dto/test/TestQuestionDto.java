package com.example.project.dto.test;

import java.util.List;

public class TestQuestionDto {
    private long questionId;
    private String questionText;
    private String questionType;
    private String questionMedia;
    private String questionAnswerDescription;
    private String answerType;
    private List<String> answersVariants;
    private Integer nextNumber;
    private Integer previousNumber;
    private boolean isLast;

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionMedia() {
        return questionMedia;
    }

    public void setQuestionMedia(String questionMedia) {
        this.questionMedia = questionMedia;
    }

    public String getQuestionAnswerDescription() {
        return questionAnswerDescription;
    }

    public void setQuestionAnswerDescription(String questionAnswerDescription) {
        this.questionAnswerDescription = questionAnswerDescription;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public List<String> getAnswersVariants() {
        return answersVariants;
    }

    public void setAnswersVariants(List<String> answersVariants) {
        this.answersVariants = answersVariants;
    }

    public Integer getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(Integer nextNumber) {
        this.nextNumber = nextNumber;
    }

    public Integer getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(Integer previousNumber) {
        this.previousNumber = previousNumber;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
