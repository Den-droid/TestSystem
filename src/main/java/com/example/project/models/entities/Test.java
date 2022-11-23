package com.example.project.models.entities;

import com.example.project.models.enums.TestDifficulty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Test {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name = "time_limit")
    private LocalTime timeLimit;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "finish_date")
    private LocalDateTime finishDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TestDifficulty testDifficulty;

    @ManyToMany(mappedBy = "assignedTests")
    private List<User> usersAssigned;

    @OneToMany(mappedBy = "test")
    private List<TestAnswer> testAnswers;

    @OneToMany(mappedBy = "test")
    private List<CurrentTest> currentTests;

    @OneToMany(mappedBy = "test")
    private List<TestQuestion> questions;

    @OneToMany(mappedBy = "test")
    private List<FinishedTest> finishedTests;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private User userCreated;

    @ManyToMany
    @JoinTable(
            name = "test_topics",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics;

    public Test() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalTime getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(LocalTime timeLimit) {
        this.timeLimit = timeLimit;
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

    public TestDifficulty getTestDifficulty() {
        return testDifficulty;
    }

    public void setTestDifficulty(TestDifficulty testDifficulty) {
        this.testDifficulty = testDifficulty;
    }

    public List<User> getUsersAssigned() {
        return usersAssigned;
    }

    public void setUsersAssigned(List<User> usersAssigned) {
        this.usersAssigned = usersAssigned;
    }

    public List<CurrentTest> getCurrentTests() {
        return currentTests;
    }

    public void setCurrentTests(List<CurrentTest> currentTests) {
        this.currentTests = currentTests;
    }

    public List<TestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestion> questions) {
        this.questions = questions;
    }

    public List<FinishedTest> getFinishedTests() {
        return finishedTests;
    }

    public void setFinishedTests(List<FinishedTest> finishedTests) {
        this.finishedTests = finishedTests;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<TestAnswer> getTestAnswers() {
        return testAnswers;
    }

    public void setTestAnswers(List<TestAnswer> testAnswers) {
        this.testAnswers = testAnswers;
    }
}
