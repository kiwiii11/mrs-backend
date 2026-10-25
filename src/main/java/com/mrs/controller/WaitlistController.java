package com.mrs.controller;

import com.mrs.dto.reservation.ReservationRequest;
import com.mrs.entity.User;
import com.mrs.service.WaitlistService;
import com.mrs.service.WaitlistService.WaitlistResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/waitlist")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitlistResponse join(@AuthenticationPrincipal User user, @Valid @RequestBody ReservationRequest req) {
        return waitlistService.join(user, req);
    }

    @GetMapping("/my")
    public List<WaitlistResponse> mine(@AuthenticationPrincipal User user) {
        return waitlistService.myWaitlist(user.getId());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> leave(@AuthenticationPrincipal User user, @PathVariable Long id) {
        waitlistService.leave(user, id);
        return ResponseEntity.noContent().build();
    }
}