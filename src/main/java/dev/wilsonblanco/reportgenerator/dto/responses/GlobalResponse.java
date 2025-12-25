package dev.wilsonblanco.reportgenerator.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


public record GlobalResponse<T>(
        LocalDateTime timestamp,
        String message,
        boolean success,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        T data
) {
    // 1. Respuesta Exitosa con Datos (GET normal)
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(LocalDateTime.now(), "Successful operation", true, data);
    }

    // 2. Mensaje personalizado con Datos
    public static <T> GlobalResponse<T> success(String message, T data) {
        return new GlobalResponse<>(LocalDateTime.now(), message, true, data);
    }

    // 3. Respuesta Exitosa sin datos (POST, PUT, DELETE)
    public static <T> GlobalResponse<T> success(String message) {
        return new GlobalResponse<>(LocalDateTime.now(), message, true, null);
    }

    // 4. Respuesta Exitosa vacía por defecto
    public static <T> GlobalResponse<T> ok() {
        return new GlobalResponse<>(LocalDateTime.now(),"Successful operation",  true, null);
    }
}
