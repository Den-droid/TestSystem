package com.example.project.dto.test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AddTestDto {
    private String name;
    private String difficulty;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private LocalTime timeLimit;
    private List<String> topics;
    private List<String> usernames;
    private Integer questionsCount;
    private String includeMe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public LocalTime getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(LocalTime timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public Integer getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
    }

    public String getIncludeMe() {
        return includeMe;
    }

    public void setIncludeMe(String includeMe) {
        this.includeMe = includeMe;
    }
}
