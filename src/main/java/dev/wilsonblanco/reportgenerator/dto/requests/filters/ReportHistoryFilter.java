package dev.wilsonblanco.reportgenerator.dto.requests.filters;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReportHistoryFilter(
        String search,
        String status,
        String format,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {
}