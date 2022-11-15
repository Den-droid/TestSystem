package com.example.project.models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tests_questions")
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private double value;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "question")
    private List<TestAnswer> answers;

    public TestQuestion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<TestAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<TestAnswer> answers) {
        this.answers = answers;
    }
}
