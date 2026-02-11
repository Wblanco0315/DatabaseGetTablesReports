package dev.wilsonblanco.reportgenerator.dto.requests.filters;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReportTemplateFilter(
        String search,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {
}
