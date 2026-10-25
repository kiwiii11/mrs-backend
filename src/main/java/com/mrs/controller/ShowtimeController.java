package com.mrs.controller;

import com.mrs.dto.reservation.SeatAvailabilityResponse;
import com.mrs.dto.showtime.ShowtimeRequest;
import com.mrs.dto.showtime.ShowtimeResponse;
import com.mrs.service.ReservationService;
import com.mrs.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final ReservationService reservationService;

    /** GET /api/showtimes  or  GET /api/showtimes?movieId=1 */
    @GetMapping
    public List<ShowtimeResponse> getAll(@RequestParam(required = false) Long movieId) {
        return movieId == null ? showtimeService.getAllShowtimes() : showtimeService.getShowtimesForMovie(movieId);
    }

    @GetMapping("/{id}")
    public ShowtimeResponse getOne(@PathVariable Long id) { return showtimeService.getShowtime(id); }

    /** Seat map with availability for this showtime. */
    @GetMapping("/{id}/seats")
    public List<SeatAvailabilityResponse> seats(@PathVariable Long id) {
        return reservationService.checkAvailability(id);
    }

    @PostMapping
    public ResponseEntity<ShowtimeResponse> create(@Valid @RequestBody ShowtimeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showtimeService.createShowtime(req));
    }

    @PutMapping("/{id}")
    public ShowtimeResponse update(@PathVariable Long id, @Valid @RequestBody ShowtimeRequest req) {
        return showtimeService.updateShowtime(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }
}
