package com.example.project.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "question_statistic")
public class QuestionStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "correct_answers")
    private int correctAnswers;
    @Column(name = "wrong_answers")
    private int wrongAnswers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
