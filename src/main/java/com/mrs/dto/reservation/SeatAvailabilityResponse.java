package com.mrs.dto.reservation;

public record SeatAvailabilityResponse(Long seatId, String theater, String rowLabel, int seatNumber, boolean available) { }
