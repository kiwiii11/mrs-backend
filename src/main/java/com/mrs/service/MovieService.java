package com.mrs.service;

import com.mrs.dto.movie.MovieRequest;
import com.mrs.dto.movie.MovieResponse;
import com.mrs.entity.Movie;
import com.mrs.exception.ResourceNotFoundException;
import com.mrs.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream().map(MovieResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long id) {
        return MovieResponse.from(find(id));
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest r) {
        return MovieResponse.from(movieRepository.save(Movie.builder()
                .title(r.title())
                .description(r.description())
                .genre(r.genre())
                .duration(r.duration())
                .rating(r.rating())
                .ageLimit(r.ageLimit())
                .build()));
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest r) {
        Movie m = find(id);
        m.setTitle(r.title());
        m.setDescription(r.description());
        m.setGenre(r.genre());
        m.setDuration(r.duration());
        m.setRating(r.rating());
        m.setAgeLimit(r.ageLimit());
        m.setPosterUrl(r.posterUrl());
        m.setPosterUrl(r.posterUrl());
        return MovieResponse.from(m);
    }

    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.delete(find(id));
    }

    private Movie find(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }
}