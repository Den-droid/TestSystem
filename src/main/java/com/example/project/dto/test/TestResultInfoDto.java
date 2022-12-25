package com.example.project.dto.test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TestResultInfoDto {
    private String name;
    private LocalTime timeLimit;
    private Integer questionsCount;
    private List<String> topics;
    private String difficulty;
    private LocalDateTime dateStarted;
    private LocalDateTime dateFinish;
    private List<String> userCompletedUsernames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(LocalTime timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(LocalDateTime dateStarted) {
        this.dateStarted = dateStarted;
    }

    public LocalDateTime getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(LocalDateTime dateFinish) {
        this.dateFinish = dateFinish;
    }

    public List<String> getUserCompletedUsernames() {
        return userCompletedUsernames;
    }

    public void setUserCompletedUsernames(List<String> userCompletedUsernames) {
        this.userCompletedUsernames = userCompletedUsernames;
    }
}
