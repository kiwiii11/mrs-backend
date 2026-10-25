package com.mrs.service;

import com.mrs.dto.reservation.ReservationRequest;
import com.mrs.entity.*;
import com.mrs.exception.ResourceNotFoundException;
import com.mrs.exception.SeatUnavailableException;
import com.mrs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.security.access.AccessDeniedException;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final ReservationService reservationService;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ShowtimeRepository showtimeRepository;

    public record WaitlistResponse(Long id, Long showtimeId, List<Long> seatIds, WaitlistStatus status) {
        static WaitlistResponse from(WaitlistEntry w) {
            return new WaitlistResponse(w.getId(), w.getShowtime().getId(), List.copyOf(w.getSeatIds()), w.getStatus());
        }
    }

    /** Join the waitlist (only allowed when at least one requested seat is currently taken). */
    @Transactional
    public WaitlistResponse join(User user, ReservationRequest req) {
        Showtime showtime = showtimeRepository.findById(req.showtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + req.showtimeId()));
        List<Long> seatIds = req.seatIds().stream().distinct().toList();

        if (reservationSeatRepository.findBookedSeatIds(req.showtimeId(), seatIds).isEmpty()) {
            throw new SeatUnavailableException("These seats are free right now, please book them directly");
        }

        WaitlistEntry entry = new WaitlistEntry();
        entry.setUser(user);
        entry.setShowtime(showtime);
        entry.setSeatIds(new java.util.ArrayList<>(seatIds));
        waitlistRepository.save(entry);
        return WaitlistResponse.from(entry);
    }

    @Transactional(readOnly = true)
    public List<WaitlistResponse> myWaitlist(Long userId) {
        return waitlistRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(WaitlistResponse::from).toList();
    }

    /** Runs right after a cancellation commits: auto-book the next people in line. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onReservationCancelled(ReservationCancelledEvent event) {
        promote(event.showtimeId());
    }

    /** Safety net: every 60s, retry all waiting entries (@Scheduled). */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void retryAllWaiting() {
        for (Long showtimeId : waitlistRepository.findShowtimeIdsByStatus(WaitlistStatus.WAITING)) {
            promote(showtimeId);
        }
    }

    /** First come, first served: book each waiting entry whose seats are all free now. */
    void promote(Long showtimeId) {
        for (WaitlistEntry w : waitlistRepository
                .findByShowtimeIdAndStatusOrderByCreatedAtAsc(showtimeId, WaitlistStatus.WAITING)) {
            if (!reservationSeatRepository.findBookedSeatIds(showtimeId, w.getSeatIds()).isEmpty()) {
                continue; // still taken, keep waiting
            }
            reservationService.reserveSeats(w.getUser(), new ReservationRequest(showtimeId, w.getSeatIds()));
            w.setStatus(WaitlistStatus.FULFILLED);
            log.info("EMAIL to {}: seats {} for showtime {} were freed and have been booked for you!",
                    w.getUser().getEmail(), w.getSeatIds(), showtimeId);
        }
    }
    /** Leave the waitlist: only the owner (or an admin), and only while still WAITING. */
    @Transactional
    public void leave(User user, Long entryId) {
        WaitlistEntry w = waitlistRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Waitlist entry not found with id: " + entryId));
        boolean owner = w.getUser().getId().equals(user.getId());
        if (!owner && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only leave your own waitlist entries");
        }
        if (w.getStatus() != WaitlistStatus.WAITING) {
            throw new SeatUnavailableException("These seats are already booked for you. Cancel it from My Bookings instead");
        }
        waitlistRepository.delete(w);
    }
}