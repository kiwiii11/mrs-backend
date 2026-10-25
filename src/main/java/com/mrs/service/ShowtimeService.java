package com.mrs.service;

import com.mrs.dto.showtime.ShowtimeRequest;
import com.mrs.dto.showtime.ShowtimeResponse;
import com.mrs.entity.Movie;
import com.mrs.entity.Showtime;
import com.mrs.exception.ResourceNotFoundException;
import com.mrs.repository.MovieRepository;
import com.mrs.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream().map(ShowtimeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getShowtimesForMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream().map(ShowtimeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ShowtimeResponse getShowtime(Long id) {
        return ShowtimeResponse.from(find(id));
    }

    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest r) {
        return ShowtimeResponse.from(showtimeRepository.save(Showtime.builder()
                .movie(findMovie(r.movieId())).theater(r.theater())
                .startTime(r.startTime()).price(r.price()).build()));
    }

    @Transactional
    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest r) {
        Showtime s = find(id);
        s.setMovie(findMovie(r.movieId()));
        s.setTheater(r.theater());
        s.setStartTime(r.startTime());
        s.setPrice(r.price());
        return ShowtimeResponse.from(s);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        showtimeRepository.delete(find(id));
    }

    private Showtime find(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
    }

    private Movie findMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }
}
