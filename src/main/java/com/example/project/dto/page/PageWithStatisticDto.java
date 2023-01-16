package com.example.project.dto.page;

import java.util.List;

public class PageWithStatisticDto<T> extends PageDto<T>{
    List<Integer> correctAnswers;

    List<Integer> wrongAnswers;

    public List<Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<Integer> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public List<Integer> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<Integer> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public PageWithStatisticDto(List<T> elements, int currentPage, int totalPages,
                                List<Integer> correctAnswers, List<Integer> wrongAnswers) {
        super(elements, currentPage, totalPages);
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }
}
