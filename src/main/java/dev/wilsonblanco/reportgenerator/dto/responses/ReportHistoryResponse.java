package dev.wilsonblanco.reportgenerator.dto.responses;

import lombok.Builder;

@Builder
public record ReportHistoryResponse(
        Long id,
        String reportName,
        String query,
        String reportFormat,
        String status,
        String generatedAt,
        String generationDuration,
        String filePath,
        String details
) {
}
