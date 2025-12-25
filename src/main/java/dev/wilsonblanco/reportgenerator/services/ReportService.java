package dev.wilsonblanco.reportgenerator.services;

import dev.wilsonblanco.reportgenerator.dto.requests.ReportRequest;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

    @Qualifier("excelExportJob")
    private final Job excelExportJob;

    @Qualifier("csvExportJob")
    private final Job csvExportJob;

    @Qualifier("asyncJobLauncher")
    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;

    public ResponseEntity<GlobalResponse> generateExcelReport(ReportRequest request) throws Exception {
        return launchJob(request, excelExportJob, ".xlsx");
    }

    public ResponseEntity<GlobalResponse> generateCsvReport(ReportRequest request) throws Exception {
        return launchJob(request, csvExportJob, ".csv");
    }

    private ResponseEntity<GlobalResponse> launchJob(ReportRequest request, Job job, String extension) throws Exception {
        // 1. Construir ruta
        String basePath = request.destinationPath();
        if (!basePath.endsWith(File.separator) && !basePath.endsWith("/")) {
            basePath += File.separator;
        }
        String fullPath = basePath + request.name() + extension;

        // 2. Parámetros
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("connectionUuid", request.connectionUuid())
                .addString("outputFilePath", fullPath)
                .addString("sqlQuery", request.sqlQuery())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // 3. Ejecutar
        var execution = jobLauncher.run(job, jobParameters);

        return ResponseEntity.ok(
                GlobalResponse.success("Reporte iniciado", Map.of(
                        "jobId", execution.getId(),
                        "status", "STARTED",
                        "path", fullPath
                ))
        );
    }


    public ResponseEntity<GlobalResponse> checkStatus(Long jobId) throws Exception {
        var execution = jobExplorer.getJobExecution(jobId);

        if (execution == null) {
            throw new Exception("No se encontró ningún trabajo con ID: " + jobId);
        }

        return ResponseEntity.ok(
                GlobalResponse.success("Estado del reporte", Map.of(
                        "job_id", jobId,
                        "status", execution.getStatus().toString(),
                        "isRunning", execution.isRunning()
                ))
        );
    }
}
