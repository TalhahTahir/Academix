package com.talha.academix.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long contentID;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="course_id", nullable=false)
    private Course course;

    private String description;

    private String image;

    @OneToMany(mappedBy="content", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Lecture> lectures;

    @OneToMany(mappedBy="content", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Document> documents;

}
