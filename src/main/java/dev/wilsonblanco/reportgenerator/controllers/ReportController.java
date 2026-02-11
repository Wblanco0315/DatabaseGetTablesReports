package dev.wilsonblanco.reportgenerator.controllers;

import dev.wilsonblanco.reportgenerator.dto.requests.ReportRequest;
import dev.wilsonblanco.reportgenerator.dto.requests.filters.ReportHistoryFilter;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/generate/excel")
    public ResponseEntity<GlobalResponse> generateExcelReport(@RequestBody ReportRequest request) throws Exception {
        return reportService.generateExcelReport(request);
    }

    @PostMapping("/generate/csv")
    public ResponseEntity<GlobalResponse> generateCsvReport(@RequestBody ReportRequest request) throws Exception {
        return reportService.generateCsvReport(request);
    }

    @GetMapping("/history")
    public ResponseEntity<GlobalResponse> getHistory(
            @ModelAttribute ReportHistoryFilter filter,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return reportService.getReportHistory(filter, pageable);
    }
}
