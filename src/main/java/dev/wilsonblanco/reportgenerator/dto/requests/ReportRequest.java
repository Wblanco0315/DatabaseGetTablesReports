package dev.wilsonblanco.reportgenerator.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank(message = "Debes seleccionar una conexión")
        String connectionUuid,

        @NotBlank(message = "El nombre del reporte es obligatorio")
        String name,

        @NotBlank(message = "Debes proporcionar una consulta SQL")
        String sqlQuery,

        @NotBlank(message = "La ruta de destino es obligatoria")
        String destinationPath // <--- EJ: "C:/Usuarios/Juan/Documentos/Reporte_Ventas.xlsx"
) {
}
