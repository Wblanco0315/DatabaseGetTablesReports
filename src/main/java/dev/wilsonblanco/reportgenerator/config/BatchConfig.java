package dev.wilsonblanco.reportgenerator.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import java.util.Map;

@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public Job exportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("exportJob", jobRepository)
                .start(exportStep)
                .build();
    }

    @Bean
    @StepScope
    public Step exportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Map<String, Object>> reader,
            ItemWriter<Map<String, Object>> writer
    ) {
        return new StepBuilder("exportStep", jobRepository)
                .<Map<String, Object>, Map<String, Object>>chunk(1000, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}