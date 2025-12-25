package dev.wilsonblanco.reportgenerator.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Validated
@Builder
public record ConnectionCredentialsDto(

        String uuid,
        @NotBlank(message = "El alias no puede estar vacío")
        @Size(min = 3, max = 50, message = "El alias debe tener entre 3 y 50 caracteres")
        String alias,

        @NotBlank(message = "Debes especificar el tipo de base de datos")
        @Pattern(regexp = "^(postgres|sqlserver)$", message = "Solo se permite 'postgres' o 'sqlserver'")
        String dbType,

        @NotBlank(message = "La IP o Host es obligatoria")
        String host,

        @NotNull(message = "El puerto es obligatorio")
        @Min(value = 1, message = "El puerto mínimo es 1")
        @Max(value = 65535, message = "El puerto máximo es 65535")
        Integer port,

        @NotBlank(message = "El nombre de la DB es obligatorio")
        String dbName,

        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {

}
