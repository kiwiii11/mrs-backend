package com.mrs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String genre;

    /** duration in minutes */
    private int duration;
    private Double rating;

    @Column(name = "age_limit", length = 10)
    private String ageLimit;

    @Column(name = "poster_url")
    private String posterUrl;

}
