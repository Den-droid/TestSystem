package com.example.project.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "test_answers")
public class TestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String answer;

    @ManyToOne
    @JoinColumn(name = "test_question_id")
    private TestQuestion question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public TestAnswer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public TestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(TestQuestion question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
