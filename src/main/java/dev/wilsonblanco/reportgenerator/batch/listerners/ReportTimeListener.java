package dev.wilsonblanco.reportgenerator.batch.listerners;

import dev.wilsonblanco.reportgenerator.models.ReportHistoryEntity;
import dev.wilsonblanco.reportgenerator.repositories.ReportHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReportTimeListener implements JobExecutionListener {

    private final ReportHistoryRepository repository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        // 1. Recuperar el ID del historial que pasamos en el paso 1
        Long historyId = jobExecution.getJobParameters().getLong("reportHistoryId");

        if (historyId == null) return;

        // 2. Calcular la duración
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        Duration duration = Duration.between(start, end);

        // Formatear la duración en un formato legible (ej: "2m 30s")
        String durationStr = String.format("%dm %ds", duration.toMinutes(), duration.toSecondsPart());

        // 3. Actualizar la base de datos
        ReportHistoryEntity history = repository.findById(historyId).orElse(null);
        if (history != null) {
            history.setStatus(jobExecution.getStatus().toString()); // COMPLETED o FAILED
            history.setGenerationDuration(durationStr);

            repository.save(history);
        }
    }
}