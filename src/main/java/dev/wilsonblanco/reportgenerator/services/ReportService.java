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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String basePath = request.destinationPath();
        String finalQuery = resolveSqlQuery(request);
        if (!basePath.endsWith(File.separator) && !basePath.endsWith("/")) {
            basePath += File.separator;
        }
        String fullPath = basePath + request.name() + extension;

        // 2. Parámetros
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("connectionUuid", request.connectionUuid())
                .addString("outputFilePath", fullPath)
                .addString("sqlQuery", finalQuery)
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

    private String resolveSqlQuery(ReportRequest request) {
        if (request.sqlQuery() != null && !request.sqlQuery().isBlank()) {
            return request.sqlQuery();
        }

        if (request.tableName() != null && !request.tableName().isBlank()
                && request.columns() != null && !request.columns().isEmpty()) {
            return buildSqlFromColumns(request.tableName(), request.columns());
        }

        throw new IllegalArgumentException("Debes enviar 'sqlQuery' O 'tableName' + 'columns'");
    }

    private String buildSqlFromColumns(String tableName, List<ReportRequest.ReportColumn> columns) {

        String columnsPart = columns.stream()
                .map(col -> {
                    String cleanName = col.name().replaceAll("[^a-zA-Z0-9_.]", "");
                    if (col.alias() != null && !col.alias().isBlank()) {
                        return String.format("%s AS \"%s\"", cleanName, col.alias());
                    } else {
                        return cleanName;
                    }
                })
                .collect(Collectors.joining(", "));

        // Resultado Final: SELECT id AS "Código", name AS "Nombre Cliente" FROM usuarios
        return String.format("SELECT %s FROM %s", columnsPart, tableName);
    }
}
