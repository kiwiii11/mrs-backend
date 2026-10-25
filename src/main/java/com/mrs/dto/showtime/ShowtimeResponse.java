package com.mrs.dto.showtime;

import com.mrs.entity.Showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowtimeResponse(Long id, Long movieId, String movieTitle, String theater,
                               LocalDateTime startTime, BigDecimal price) {
    public static ShowtimeResponse from(Showtime s) {
        return new ShowtimeResponse(s.getId(), s.getMovie().getId(), s.getMovie().getTitle(),
                s.getTheater(), s.getStartTime(), s.getPrice());
    }
}
