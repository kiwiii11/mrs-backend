package com.mrs.service;

import com.mrs.dto.reservation.ReservationRequest;
import com.mrs.dto.reservation.ReservationResponse;
import com.mrs.dto.reservation.SeatAvailabilityResponse;
import com.mrs.entity.*;
import com.mrs.exception.ResourceNotFoundException;
import com.mrs.exception.SeatUnavailableException;
import com.mrs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final org.springframework.context.ApplicationEventPublisher events;

    /** Every seat in the showtime's theater, flagged available/booked for THIS showtime. */
    @Transactional(readOnly = true)
    public List<SeatAvailabilityResponse> checkAvailability(Long showtimeId) {
        Showtime showtime = findShowtime(showtimeId);
        Set<Long> booked = Set.copyOf(reservationSeatRepository.findAllBookedSeatIdsForShowtime(showtimeId));
        return seatRepository.findByTheater(showtime.getTheater()).stream()
                .sorted(Comparator.comparing(Seat::getRowLabel).thenComparing(Seat::getSeatNumber))
                .map(s -> new SeatAvailabilityResponse(s.getId(), s.getTheater(), s.getRowLabel(),
                        s.getSeatNumber(), !booked.contains(s.getId())))
                .toList();
    }

    /**
     * Transactional reservation (FR4, FR5, NFR2).
     * Seat rows are locked with SELECT ... FOR UPDATE, so a concurrent request for the
     * same seats waits, then re-checks availability and gets a 409 Conflict.
     */
    @Transactional
    public ReservationResponse reserveSeats(User user, ReservationRequest req) {
        Showtime showtime = findShowtime(req.showtimeId());
        List<Long> seatIds = req.seatIds().stream().distinct().toList();

        List<Seat> seats = seatRepository.lockAllByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("One or more requested seats do not exist");
        }
        if (seats.stream().anyMatch(s -> !s.getTheater().equals(showtime.getTheater()))) {
            throw new SeatUnavailableException("One or more seats do not belong to this showtime's theater");
        }

        List<Long> taken = reservationSeatRepository.findBookedSeatIds(req.showtimeId(), seatIds);
        if (!taken.isEmpty()) {
            throw new SeatUnavailableException("Seat(s) already booked for this showtime: " + taken);
        }

        Reservation reservation = Reservation.builder()
                .user(user).showtime(showtime).status(ReservationStatus.CONFIRMED).build();
        List<ReservationSeat> links = new ArrayList<>();
        for (Seat seat : seats) {
            links.add(ReservationSeat.builder().reservation(reservation).seat(seat).build());
        }
        reservation.setReservationSeats(links);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream().map(ReservationResponse::from).toList();
    }

    /** Soft-cancel: seats become available again because only CONFIRMED rows count as booked. */
    @Transactional
    public void cancelReservation(User user, Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        boolean owner = r.getUser().getId().equals(user.getId());
        if (!owner && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only cancel your own reservations");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        events.publishEvent(new ReservationCancelledEvent(r.getShowtime().getId()));

    }

    private Showtime findShowtime(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
    }
}
