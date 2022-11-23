package com.example.project.models.mappers;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.models.entities.Question;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;

public class AddQuestionMapper {
    public static Question map(AddQuestionDto dto) {
        Question question = new Question();
        question.setText(dto.getQuestionText());
        question.setAnswerDescription(dto.getAnswerDescription());
        question.setDifficulty(QuestionDifficulty.valueOf(dto.getQuestionDifficulty()));
        question.setAnswerType(AnswerType.valueOf(dto.getAnswerType()));
        question.setType(QuestionType.valueOf(dto.getQuestionType()));
        question.setCoefficient(1);
        if (question.getAnswerType() == AnswerType.MATCH) {

        } else if (question.getAnswerType() == AnswerType.SINGLE) {

        } else if (question.getAnswerType() == AnswerType.MULTIPLE) {

        } else if (question.getAnswerType() == AnswerType.CUSTOM) {

        }
        return question;
    }
}
