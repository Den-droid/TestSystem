package com.example.project.dto.test;

import java.util.List;

public class TestQuestionDto {
    private long questionId;
    private String questionText;
    private List<String> subQuestionsText;
    private String questionMedia;
    private String questionAnswerDescription;
    private String answerType;
    private List<String> answers;

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

    public List<String> getSubQuestionsText() {
        return subQuestionsText;
    }

    public void setSubQuestionsText(List<String> subQuestionsText) {
        this.subQuestionsText = subQuestionsText;
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

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}
