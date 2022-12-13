package com.example.project.models.entities;

import com.example.project.models.enums.TestDifficulty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
public class Test {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    @Column(name = "time_limit")
    private LocalTime timeLimit;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "finish_date")
    private LocalDateTime finishDate;
    @Column(name = "questions_count")
    private Integer questionsCount;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TestDifficulty difficulty;

    @OneToMany(mappedBy = "test")
    private List<TestAnswer> testAnswers;

    @OneToMany(mappedBy = "test")
    private List<CurrentTest> currentTests;

    @OneToMany(mappedBy = "test")
    private List<TestQuestion> questions;

    @OneToMany(mappedBy = "test")
    private List<FinishedTest> finishedTests;

    @ManyToOne
    @JoinColumn(name = "user_created_id", referencedColumnName = "id")
    private User userCreated;

    @ManyToMany
    @JoinTable(
            name = "test_topics",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics;

    @ManyToMany
    @JoinTable(
            name = "test_users_assigned",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersAssigned;

    public Test() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
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

    public TestDifficulty getDifficulty() {
        return difficulty;
    }

    public void setTestDifficulty(TestDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Set<User> getUsersAssigned() {
        return usersAssigned;
    }

    public void setUsersAssigned(Set<User> usersAssigned) {
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

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public List<TestAnswer> getTestAnswers() {
        return testAnswers;
    }

    public void setTestAnswers(List<TestAnswer> testAnswers) {
        this.testAnswers = testAnswers;
    }
}
