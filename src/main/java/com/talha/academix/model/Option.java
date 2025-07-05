package com.talha.academix.model;

import jakarta.persistence.*;

@Entity
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}

