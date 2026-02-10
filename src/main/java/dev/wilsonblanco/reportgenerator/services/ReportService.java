package dev.wilsonblanco.reportgenerator.services;

import dev.wilsonblanco.reportgenerator.dto.requests.ReportRequest;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.models.ReportHistoryEntity;
import dev.wilsonblanco.reportgenerator.repositories.ReportHistoryEntityRepository;
import dev.wilsonblanco.reportgenerator.repositories.ReportTemplatesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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

    @Autowired
    private final ReportTemplatesRepository reportTemplatesRepository;

    @Autowired
    private final ReportHistoryEntityRepository reportHistoryEntityRepository;

    private static final Pattern FORBIDDEN_KEYWORDS = Pattern.compile(
            "(?i)\\b(INSERT|UPDATE|DELETE|DROP|TRUNCATE|ALTER|CREATE|GRANT|REVOKE|EXEC|MERGE|REPLACE|CALL)\\b"
    );

    // Obliga a que la consulta empiece por SELECT o WITH (ignorando mayúsculas)
    private static final Pattern MUST_START_WITH_SELECT = Pattern.compile(
            "(?i)^\\s*(SELECT|WITH|DECLARE)\\b.*", Pattern.DOTALL
    );

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

        Long historyId = newReportHistory(request, fullPath, finalQuery);

        // 2. Parámetros
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("connectionUuid", request.connectionUuid())
                .addLong("reportHistoryId", historyId)
                .addString("outputFilePath", fullPath)
                .addString("sqlQuery", finalQuery)
                .addLong("timestamp", System.currentTimeMillis())
                //.addString("webhookUrl", request.webhookUrl())
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

    private String resolveSqlQuery(ReportRequest request) {

        //El usuario envió SQL crudo
        if (request.sqlQuery() != null && !request.sqlQuery().isBlank()) {
            String sql = request.sqlQuery();
            validateReadOnlySql(sql); // <--- AQUÍ SE APLICA EL FILTRO DE SEGURIDAD
            return sql;
        }

        //El usuario envió Tabla y Columnas
        if (request.tableName() != null && !request.tableName().isBlank()
                && request.columns() != null && !request.columns().isEmpty()) {
            return buildSqlFromColumns(request.tableName(), request.columns());
        }

        throw new IllegalArgumentException("Debes proporcionar 'sqlQuery' O una combinación de 'tableName' y 'columns'.");
    }

    private void validateReadOnlySql(String sql) {
        String cleanSql = sql.trim();

        // 1. Validar inicio (Case Insensitive gracias a (?i) en el patrón)
        if (!MUST_START_WITH_SELECT.matcher(cleanSql).matches()) {
            throw new SecurityException("Por seguridad, la consulta debe comenzar con 'SELECT' o 'WITH'.");
        }

        // 2. Buscar palabras prohibidas (Case Insensitive gracias a (?i))
        if (FORBIDDEN_KEYWORDS.matcher(cleanSql).find()) {
            throw new SecurityException("La consulta contiene comandos prohibidos (UPDATE, DELETE, DROP, etc).");
        }

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

        return String.format("SELECT %s FROM %s", columnsPart, tableName);
    }

    public ResponseEntity<GlobalResponse> createReportTemplate() {


        return ResponseEntity.ok(
                GlobalResponse.success("Plantilla creada")
        );
    }

    public ResponseEntity<GlobalResponse> listReportTemplates() {

        return ResponseEntity.ok(
                GlobalResponse.success("Listado de plantillas")
        );
    }

    public ResponseEntity<GlobalResponse> deleteReportTemplate(Long templateId) {
        return ResponseEntity.ok(
                GlobalResponse.success("Plantilla eliminada", Map.of())
        );
    }

    @Transactional
    protected Long newReportHistory(ReportRequest reportRequest, String fullPath, String Query) {

        LOGGER.info("Creando registro histórico para reporte: {}", reportRequest.name());

        ReportHistoryEntity history = ReportHistoryEntity.builder()
                .reportName(reportRequest.name())
                .reportFormat(fullPath.endsWith(".xlsx") ? "XLSX" : "CSV")
                .connectionUuid(reportRequest.connectionUuid())
                .filePath(fullPath)
                .status("PENDING")
                .query(Query)
                .generationDuration("0s")
                .build();

        ReportHistoryEntity saved = reportHistoryEntityRepository.save(history);
        return saved.getId();
    }
}


