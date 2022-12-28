package com.example.project.dto.question;

import java.util.List;

public class AddQuestionPageDto {
    private List<String> questionTypes;
    private List<String> questionDifficulties;
    private List<String> answerTypes;

    public List<String> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<String> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public List<String> getQuestionDifficulties() {
        return questionDifficulties;
    }

    public void setQuestionDifficulties(List<String> questionDifficulties) {
        this.questionDifficulties = questionDifficulties;
    }

    public List<String> getAnswerTypes() {
        return answerTypes;
    }

    public void setAnswerTypes(List<String> answerTypes) {
        this.answerTypes = answerTypes;
    }
}
