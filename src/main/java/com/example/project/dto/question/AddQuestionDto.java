package com.example.project.dto.question;

public class AddQuestionDto {
    private String questionType;
    private String questionText;
    private String questionDifficulty;
    private String answerDescription;
    private String answerType;
    private String[] subQuestions;
    private String[] answers;
    private String[] isCorrect;

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(String questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String[] getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(String[] subQuestions) {
        this.subQuestions = subQuestions;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public String[] getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String[] isCorrect) {
        this.isCorrect = isCorrect;
    }
}
