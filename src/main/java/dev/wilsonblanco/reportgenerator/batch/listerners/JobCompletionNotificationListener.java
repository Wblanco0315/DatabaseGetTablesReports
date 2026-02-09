package dev.wilsonblanco.reportgenerator.batch.listerners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void afterJob(JobExecution jobExecution) {
        // 1. Recuperar la URL que guardamos en los parámetros
        String webhookUrl = jobExecution.getJobParameters().getString("webhookUrl");

        // Si no hay URL, no hacemos nada (el usuario no pidió notificación)
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        // 2. Preparar el payload a enviar
        var payload = Map.of(
                "jobId", jobExecution.getJobId(),
                "status", jobExecution.getStatus().toString(),
                "startTime", jobExecution.getStartTime().toString(),
                "endTime", jobExecution.getEndTime().toString(),
                "errors", jobExecution.getAllFailureExceptions().stream().map(Throwable::getMessage).toList()
        );

        // 3. Enviar el POST (Disparar el Webhook)
        try {
            LOGGER.info("Enviando Webhook a: {}", webhookUrl);
            restTemplate.postForEntity(webhookUrl, payload, Void.class);
        } catch (Exception e) {
            LOGGER.error("Error al enviar el webhook", e);
        }
    }
}