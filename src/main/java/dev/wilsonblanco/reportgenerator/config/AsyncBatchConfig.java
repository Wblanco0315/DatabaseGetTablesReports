package dev.wilsonblanco.reportgenerator.config;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncBatchConfig {

    @Bean(name = "batchTaskExecutor")
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Configuración de la "Cola":
        executor.setCorePoolSize(3);   // 3 reportes procesándose a la vez
        executor.setMaxPoolSize(5);    // Máximo 5 si hay mucha carga
        executor.setQueueCapacity(100);// Hasta 100 en espera si los 5 hilos están ocupados
        executor.setThreadNamePrefix("Batch-Job-");
        executor.initialize();
        return executor;
    }

    // Sobrescribimos el JobLauncher para que use nuestro ejecutor asíncrono
    @Bean(name = "asyncJobLauncher")
    public JobLauncher asyncJobLauncher(JobRepository jobRepository, TaskExecutor batchTaskExecutor) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(batchTaskExecutor); // ¡Aquí está la clave!
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}