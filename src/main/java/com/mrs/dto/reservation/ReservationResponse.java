package com.mrs.dto.reservation;

import com.mrs.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(Long id, Long showtimeId, String movieTitle, String theater,
                                  LocalDateTime startTime, List<String> seats, String status,
                                  LocalDateTime createdAt) {
    public static ReservationResponse from(Reservation r) {
        List<String> labels = r.getReservationSeats().stream()
                .map(rs -> rs.getSeat().getRowLabel() + rs.getSeat().getSeatNumber())
                .toList();
        return new ReservationResponse(r.getId(), r.getShowtime().getId(),
                r.getShowtime().getMovie().getTitle(), r.getShowtime().getTheater(),
                r.getShowtime().getStartTime(), labels, r.getStatus().name(), r.getCreatedAt());
    }
}
