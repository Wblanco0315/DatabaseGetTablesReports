package dev.wilsonblanco.reportgenerator.controllers;

import dev.wilsonblanco.reportgenerator.dto.requests.ReportRequest;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.services.ReportService;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/generate/excel")
    public ResponseEntity<GlobalResponse> generateExcelReport(@RequestBody ReportRequest request) throws Exception {
        return reportService.generateExcelReport(request);
    }
}
