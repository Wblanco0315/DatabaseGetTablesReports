package dev.wilsonblanco.reportgenerator.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Validated
@Builder
public record ReportTemplateRequest(
        Long id,
        @NotBlank(message = "El nombre de la plantilla es obligatorio")
        String templateName,
        @NotBlank(message = "El contenido de la plantilla es obligatorio")
        String templateContent,
        @NotBlank(message = "La descripción de la plantilla es obligatoria")
        String description,
        Timestamp createdAt,
        Timestamp updatedAt

) {

}
