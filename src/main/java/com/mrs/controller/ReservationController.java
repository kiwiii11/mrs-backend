package com.mrs.controller;

import com.mrs.dto.reservation.ReservationRequest;
import com.mrs.dto.reservation.ReservationResponse;
import com.mrs.entity.User;
import com.mrs.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.reserveSeats(user, req));
    }

    @GetMapping("/my")
    public List<ReservationResponse> mine(@AuthenticationPrincipal User user) {
        return reservationService.getMyReservations(user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal User user, @PathVariable Long id) {
        reservationService.cancelReservation(user, id);
        return ResponseEntity.noContent().build();
    }
}
