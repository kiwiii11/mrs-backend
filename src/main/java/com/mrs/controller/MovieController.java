package com.mrs.controller;

import com.mrs.dto.movie.MovieRequest;
import com.mrs.dto.movie.MovieResponse;
import com.mrs.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public List<MovieResponse> getAll() { return movieService.getAllMovies(); }

    @GetMapping("/{id}")
    public MovieResponse getOne(@PathVariable Long id) { return movieService.getMovie(id); }

    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(req));
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest req) {
        return movieService.updateMovie(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
