package com.example.project.models.entities;

import com.example.project.models.enums.Role;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Topic> topics;

    @OneToMany(mappedBy = "user")
    private List<Question> questions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CurrentTest> currentTests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<FinishedTest> finishedTests;

    @OneToMany(mappedBy = "userCreated")
    private List<Test> testsCreated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<TestAnswer> testAnswers;

    @ManyToMany(mappedBy = "usersAssigned")
    private Set<Test> assignedTests;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<CurrentTest> getCurrentTests() {
        return currentTests;
    }

    public void setCurrentTests(List<CurrentTest> currentTests) {
        this.currentTests = currentTests;
    }

    public List<FinishedTest> getFinishedTests() {
        return finishedTests;
    }

    public void setFinishedTests(List<FinishedTest> finishedTests) {
        this.finishedTests = finishedTests;
    }

    public List<Test> getTestsCreated() {
        return testsCreated;
    }

    public void setTestsCreated(List<Test> testsCreated) {
        this.testsCreated = testsCreated;
    }

    public List<TestAnswer> getTestAnswers() {
        return testAnswers;
    }

    public void setTestAnswers(List<TestAnswer> testAnswers) {
        this.testAnswers = testAnswers;
    }

    public Set<Test> getAssignedTests() {
        return assignedTests;
    }

    public void setAssignedTests(Set<Test> assignedTests) {
        this.assignedTests = assignedTests;
    }
}
