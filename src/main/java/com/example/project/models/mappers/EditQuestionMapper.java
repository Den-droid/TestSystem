package com.example.project.models.mappers;

import com.example.project.dto.question.EditQuestionDto;
import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditQuestionMapper {
    public static Question map(EditQuestionDto dto) {
        Question question = new Question();
        question.setText(dto.getQuestionText().trim());
        question.setAnswerDescription(dto.getAnswerDescription().trim());
        question.setAnswerType(AnswerType.getByText(dto.getAnswerType()));
        question.setType(QuestionType.getByText(dto.getQuestionType()));

        QuestionDifficulty questionDifficulty = QuestionDifficulty
                .getByText(dto.getQuestionDifficulty());
        question.setDifficulty(questionDifficulty);
        question.setLastCorrectAnswerCoefficient(
                questionDifficulty.getCoefficientThreshold());
        if (question.getAnswerType() == AnswerType.MATCH) {
            List<Question> subQuestions = new ArrayList<>();
            for (int i = 0; i < dto.getSubQuestions().length; i++) {
                Question subQuestion = new Question();
                if (!dto.getSubQuestionIds()[i].isEmpty())
                    subQuestion.setId(Long.parseLong(dto.getSubQuestionIds()[i]));
                subQuestion.setText(dto.getSubQuestions()[i]);
                subQuestion.setSupQuestion(question);
                subQuestion.setLastCorrectAnswerCoefficient(0);

                Answer answer = new Answer();
                if (!dto.getAnswerIds()[i].isEmpty()) {
                    answer.setId(Long.parseLong(dto.getAnswerIds()[i]));
                }

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
                if (!dto.getAnswerIds()[i].isEmpty()) {
                    answer.setId(Long.parseLong(dto.getAnswerIds()[i]));
                }

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
                if (!dto.getAnswerIds()[i].isEmpty()) {
                    answer.setId(Long.parseLong(dto.getAnswerIds()[i]));
                }

                answer.setText(dto.getAnswers()[i]);
                answer.setQuestion(question);

                int index = i;
                answer.setCorrect(Arrays.stream(dto.getIsCorrect())
                        .anyMatch(x -> Integer.parseInt(x) == index));
                answers.add(answer);
            }
            question.setAnswers(answers);
        } else if (question.getAnswerType() == AnswerType.CUSTOM) {
            List<Answer> answers = new ArrayList<>();
            for (int i = 0; i < dto.getAnswers().length; i++) {
                Answer answer = new Answer();
                if (!dto.getAnswerIds()[i].isEmpty()) {
                    answer.setId(Long.parseLong(dto.getAnswerIds()[i]));
                }

                answer.setText(dto.getAnswers()[i]);
                answer.setQuestion(question);
                answer.setCorrect(true);
                answers.add(answer);
            }
            question.setAnswers(answers);
        }
        return question;
    }
}
