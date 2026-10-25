package com.mrs.service;

import com.mrs.dto.report.ShowtimeReportResponse;
import com.mrs.entity.Showtime;
import com.mrs.repository.ReservationRepository;
import com.mrs.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ShowtimeRepository showtimeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<ShowtimeReportResponse> getReportPerShowtime() {
        return showtimeRepository.findAll().stream().map(this::build).toList();
    }

    @Transactional(readOnly = true)
    public List<ShowtimeReportResponse> getReportForMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream().map(this::build).toList();
    }

    private ShowtimeReportResponse build(Showtime s) {
        long booked = reservationRepository.countBookedSeatsForShowtime(s.getId());
        BigDecimal revenue = s.getPrice().multiply(BigDecimal.valueOf(booked));
        return new ShowtimeReportResponse(s.getId(), s.getMovie().getTitle(), s.getTheater(), booked, revenue);
    }
}
