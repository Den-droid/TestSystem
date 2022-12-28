package com.example.project.dto.question;

import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;

import java.util.List;

public class EditQuestionPageDto {
    private List<String> questionTypes;
    private List<String> questionDifficulties;
    private List<String> answerTypes;
    private List<Answer> answers;
    private List<Question> subQuestions;

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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Question> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(List<Question> subQuestions) {
        this.subQuestions = subQuestions;
    }
}
