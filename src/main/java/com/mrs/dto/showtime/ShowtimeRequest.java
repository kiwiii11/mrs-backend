package com.mrs.dto.showtime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowtimeRequest(
        @NotNull Long movieId,
        @NotBlank String theater,
        @NotNull LocalDateTime startTime,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) { }
