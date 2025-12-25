package dev.wilsonblanco.reportgenerator.controllers;

import dev.wilsonblanco.reportgenerator.dto.requests.ReportRequest;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/status/{jobId}")
    public ResponseEntity<GlobalResponse> checkStatus(@PathVariable Long jobId) throws Exception {
        return reportService.checkStatus(jobId);
    }

    @PostMapping("/generate/csv")
    public ResponseEntity<GlobalResponse> generateCsvReport(@RequestBody ReportRequest request) throws Exception {
        return reportService.generateCsvReport(request);
    }
}
