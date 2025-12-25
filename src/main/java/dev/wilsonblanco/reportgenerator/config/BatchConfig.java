package dev.wilsonblanco.reportgenerator.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import java.util.Map;

@Configuration
public class BatchConfig {

    // --- JOB EXCEL ---
    @Bean
    public Job excelExportJob(JobRepository jobRepository, @Qualifier("excelExportStep") Step exportStep) {
        return new JobBuilder("excelExportJob", jobRepository)
                .start(exportStep)
                .build();
    }

    @Bean
    public Step excelExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Map<String, Object>> reader, // El reader es el mismo (DynamicJdbcReader)
            @Qualifier("excelStreamWriter") ItemWriter<Map<String, Object>> writer // Inyectamos ExcelWriter
    ) {
        return new StepBuilder("excelExportStep", jobRepository)
                .<Map<String, Object>, Map<String, Object>>chunk(1000, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    // --- JOB CSV (NUEVO) ---
    @Bean
    public Job csvExportJob(JobRepository jobRepository, @Qualifier("csvExportStep") Step exportStep) {
        return new JobBuilder("csvExportJob", jobRepository)
                .start(exportStep)
                .build();
    }

    @Bean
    public Step csvExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Map<String, Object>> reader,
            @Qualifier("csvStreamWriter") ItemWriter<Map<String, Object>> writer // Inyectamos CsvWriter
    ) {
        return new StepBuilder("csvExportStep", jobRepository)
                .<Map<String, Object>, Map<String, Object>>chunk(1000, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}