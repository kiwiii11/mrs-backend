package com.mrs.dto.movie;

import com.mrs.entity.Movie;

public record MovieResponse(
        Long id,
        String title,
        String description,
        String genre,
        int duration,
        Double rating,
        String ageLimit,
        String posterUrl
) {
    public static MovieResponse from(Movie m) {
        return new MovieResponse(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getAgeLimit(),
                m.getPosterUrl()
        );
    }
}