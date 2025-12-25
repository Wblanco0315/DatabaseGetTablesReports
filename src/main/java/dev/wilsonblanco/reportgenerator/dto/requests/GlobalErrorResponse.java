package dev.wilsonblanco.reportgenerator.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Builder
public record GlobalErrorResponse(
        LocalDateTime timestamp,
        String message,
        boolean success,
        String error,
        String path,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<ValidationError> details
) {
    // Sin detalles
    public static GlobalErrorResponse create(int status, String error, String message, String path) {
        return new GlobalErrorResponse(LocalDateTime.now(), message, false, error, path, null);
    }

    // Con detalles
    public static GlobalErrorResponse create(int status, String error, String message, String path, List<ValidationError> details) {
        return new GlobalErrorResponse(LocalDateTime.now(), message, false, error, path, details);
    }

    // Campos de detalle para formularios
    public record ValidationError(String field, String message) {
    }
}
