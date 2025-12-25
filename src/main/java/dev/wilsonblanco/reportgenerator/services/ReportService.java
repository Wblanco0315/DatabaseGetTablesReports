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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);
    private final Job exportJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    public ResponseEntity<GlobalResponse> generateExcelReport(ReportRequest request) throws Exception {
        LOGGER.info("Try Generating excel report job started for connectionUuid: {}", request.connectionUuid());

        String fullPath =  request.destinationPath()+ request.name()+ ".xlsx";

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("connectionUuid", request.connectionUuid())
                .addString("outputFilePath", fullPath)
                .addString("sqlQuery", request.sqlQuery())
                .addLong("timestamp", System.currentTimeMillis()) // Para asegurar unicidad
                .toJobParameters();

        var jobExecution = jobLauncher.run(exportJob, jobParameters);

        LOGGER.info("Generating excel report job  for connectionUuid: {}", request.connectionUuid());
        return ResponseEntity.ok(
                GlobalResponse.success("excel report job has been started")
        );
    }

    public ResponseEntity<GlobalResponse> generateCsvReport(ReportRequest request) throws Exception {
        LOGGER.info("Try Generating csv report job started for connectionUuid: {}", request.connectionUuid());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("connectionUuid", request.connectionUuid())
                .addString("outputFilePath", request.destinationPath())
                .addString("sqlQuery", request.sqlQuery())
                .addLong("timestamp", System.currentTimeMillis()) // Para asegurar unicidad
                .toJobParameters();


        var jobExecution = jobLauncher.run(exportJob, jobParameters);

        LOGGER.info("Generating csv report job  for connectionUuid: {}", request.connectionUuid());
        return ResponseEntity.ok(
                GlobalResponse.success("csv report job has been started")
        );
    }
}
