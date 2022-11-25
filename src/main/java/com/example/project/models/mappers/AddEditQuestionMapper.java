package com.example.project.models.mappers;

import com.example.project.dto.question.AddEditQuestionDto;
import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddEditQuestionMapper {
    public static Question map(AddEditQuestionDto dto) {
        Question question = new Question();
        question.setText(dto.getQuestionText().trim());
        question.setAnswerDescription(dto.getAnswerDescription().trim());
        question.setDifficulty(QuestionDifficulty.getByText(dto.getQuestionDifficulty()));
        question.setAnswerType(AnswerType.getByText(dto.getAnswerType()));
        question.setType(QuestionType.getByText(dto.getQuestionType()));
        question.setCoefficient(1);
        if (question.getAnswerType() == AnswerType.MATCH) {
            List<Question> subQuestions = new ArrayList<>();
            for (int i = 0; i < dto.getSubQuestions().length; i++) {
                Question subQuestion = new Question();
                subQuestion.setText(dto.getSubQuestions()[i]);
                subQuestion.setSupQuestion(question);

                Answer answer = new Answer();
                answer.setText(dto.getAnswers()[i]);
                answer.setCorrect(true);
                answer.setQuestion(subQuestion);

                subQuestion.setAnswers(Collections.singletonList(answer));
                subQuestions.add(subQuestion);
            }
            question.setSubQuestions(subQuestions);
        } else if (question.getAnswerType() == AnswerType.SINGLE) {
            List<Answer> answers = new ArrayList<>();
            for (int i = 0; i < dto.getAnswers().length; i++) {
                Answer answer = new Answer();
                answer.setText(dto.getAnswers()[i]);
                answer.setQuestion(question);
                answer.setCorrect(Integer.parseInt(dto.getIsCorrect()[0]) == i);
                answers.add(answer);
            }
            question.setAnswers(answers);
        } else if (question.getAnswerType() == AnswerType.MULTIPLE) {
            List<Answer> answers = new ArrayList<>();
            for (int i = 0; i < dto.getAnswers().length; i++) {
                Answer answer = new Answer();
                answer.setText(dto.getAnswers()[i]);
                answer.setQuestion(question);
                int index = i;
                answer.setCorrect(Arrays.stream(dto.getIsCorrect())
                        .anyMatch(x -> Integer.parseInt(x) == index));
                answers.add(answer);
            }
            question.setAnswers(answers);
        } else if (question.getAnswerType() == AnswerType.CUSTOM) {
            Answer answer = new Answer();
            answer.setText(dto.getAnswers()[0]);
            answer.setQuestion(question);
            answer.setCorrect(true);
            question.setAnswers(Collections.singletonList(answer));
        }
        return question;
    }
}
