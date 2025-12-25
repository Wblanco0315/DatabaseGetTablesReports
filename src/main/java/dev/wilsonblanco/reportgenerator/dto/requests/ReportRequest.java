package dev.wilsonblanco.reportgenerator.dto.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ReportRequest(
        @NotBlank(message = "Debes seleccionar una conexión")
        String connectionUuid,

        @NotBlank(message = "El nombre del reporte es obligatorio")
        String name,

        @NotBlank(message = "La ruta de destino es obligatoria")
        String destinationPath,

        String sqlQuery,

        String tableName,

        List<ReportColumn> columns
) {

    public record ReportColumn(
            String name,
            String alias
    ) {
    }
}