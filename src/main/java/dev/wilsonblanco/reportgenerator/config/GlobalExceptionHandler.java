package dev.wilsonblanco.reportgenerator.config;

import dev.wilsonblanco.reportgenerator.dto.requests.GlobalErrorResponse;
import dev.wilsonblanco.reportgenerator.exceptions.DbConnectionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejador de errores de validación de Spring
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Convertimos la lista de errores de Spring a nuestra lista limpia ValidationError
        List<GlobalErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> new GlobalErrorResponse.ValidationError(
                        ((FieldError) error).getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        GlobalErrorResponse response = GlobalErrorResponse.create(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "field validation error",
                request.getRequestURI(),
                validationErrors
        );

        LOGGER.error(response.toString());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGeneralExceptions(Exception ex, HttpServletRequest request) {

        GlobalErrorResponse response = GlobalErrorResponse.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        LOGGER.error(response.toString());

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(DbConnectionException.class)
    public ResponseEntity<GlobalErrorResponse> handleDbConnectionException(DbConnectionException ex, HttpServletRequest request) {

        GlobalErrorResponse response = GlobalErrorResponse.create(
                HttpStatus.BAD_REQUEST.value(),
                "Database Connection Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        LOGGER.error(response.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
