package com.mrs.dto.reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationRequest(@NotNull Long showtimeId, @NotEmpty List<Long> seatIds) { }
