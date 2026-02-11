package dev.wilsonblanco.reportgenerator.exceptions;

import org.springframework.http.HttpStatus;

public class ReportServiceException extends RuntimeException {
    private final HttpStatus status;

    public ReportServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}
