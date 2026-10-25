package com.mrs.controller;

import com.mrs.dto.report.ShowtimeReportResponse;
import com.mrs.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/showtimes")
    public List<ShowtimeReportResponse> perShowtime() { return reportService.getReportPerShowtime(); }

    @GetMapping("/movies/{movieId}")
    public List<ShowtimeReportResponse> perMovie(@PathVariable Long movieId) {
        return reportService.getReportForMovie(movieId);
    }
}
