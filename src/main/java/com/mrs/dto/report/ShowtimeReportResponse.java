package com.mrs.dto.report;

import java.math.BigDecimal;

public record ShowtimeReportResponse(Long showtimeId, String movieTitle, String theater,
                                     long seatsBooked, BigDecimal revenue) { }
