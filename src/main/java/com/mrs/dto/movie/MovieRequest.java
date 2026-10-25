package com.mrs.dto.movie;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MovieRequest(
        @NotBlank String title,
        String description,
        String genre,
        @Min(1) int duration,
        @DecimalMin("0.0") @DecimalMax("10.0") Double rating,
        @Size(max = 10) String ageLimit,
        @Size(max = 255) String posterUrl
) { }